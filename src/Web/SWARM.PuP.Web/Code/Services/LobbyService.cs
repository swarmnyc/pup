using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.Services
{
    public class LobbyService : BaseService<Lobby>, ILobbyService
    {
        private readonly IChatService _chatService;
        private readonly IUserService _userService;

        public LobbyService(IChatService chatService, IUserService userService) : base("Lobbies")
        {
            _chatService = chatService;
            _userService = userService;
        }

        public Lobby Add(Lobby lobby, PuPUser owner)
        {
            lobby.Users.Add(new LobbyUserInfo
            {
                Id = owner.Id,
                IsOwner = true,
                PortraitUrl = owner.PortraitUrl,
                UserName = owner.GetUserName(lobby.Platform)
            });

            _chatService.CreateRoomForLobby(owner, lobby);

            NotificationHubHelper.SendLobbyStart(lobby, owner);

            return base.Add(lobby);
        }

        public LobbySearchResult Filter(LobbyFilter filter)
        {
            var query = All();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(x => x.Name.ToLower().Contains(filter.Search));
            }

            if (!string.IsNullOrWhiteSpace(filter.GameId))
            {
                query = query.Where(x => x.GameId == filter.GameId);
            }

            if (filter.UserName.IsNotNullOrWhiteSpace())
            {
                filter.UserId = _userService.GetSingle(x => x.UserName.ToLower() == filter.UserName.ToLower()).Id;
            }

            if (!string.IsNullOrWhiteSpace(filter.UserId))
            {
                query = query.Where(x => x.Users.Any(y => !y.IsLeave && y.Id == filter.UserId));
            }

            if (!filter.PlayStyles.IsNullOrEmpty())
            {
                query = query.Where(x => x.PlayStyle.In(filter.PlayStyles));
            }

            if (!filter.SkillLevels.IsNullOrEmpty())
            {
                query = query.Where(x => x.SkillLevel.In(filter.SkillLevels));
            }

            if (!filter.Platforms.IsNullOrEmpty())
            {
                query = query.Where(x => x.Platform.In(filter.Platforms));
            }

            if (filter.StartTimeUtc.HasValue)
            {
                query = query.Where(x => x.StartTimeUtc >= filter.StartTimeUtc);
            }

            query = query.Where(x => x.State == ModelState.Active);

            query = DoOrderQuery(query, filter);

            LobbySearchResult result = new LobbySearchResult();
            result.Result = query.ToArray();

            if (filter.NeedCount && filter.StartTimeUtc.HasValue)
            {
                // get count
                // TODO: Better query. it might have performance issue, but maybe better than 4 query commands. 
                DateTime local = DateTime.UtcNow.AddHours(filter.TimeZone);
                DateTime tomorrow = local.AddDays(1).Date.AddHours(-filter.TimeZone); //local to utc
                DateTime in2Day2 = tomorrow.AddDays(1);
                DateTime in7Day2 = tomorrow.AddDays(6);

                GroupArgs args = new GroupArgs();
                args.Query = query.ToMongoQuery();
                args.Initial = new BsonDocument(new Dictionary<string, object>() { { "count", 0 } });
                args.ReduceFunction = new BsonJavaScript("function( current, result ) { result.count++; }");
                args.KeyFunction = new BsonJavaScript(string.Format(
@"function(doc){{
  	if(doc.StartTimeUtc >= ISODate('{2:yyyy-MM-ddTHH:mm:ssZ}')) return {{ type:3 }};
	if(doc.StartTimeUtc >= ISODate('{1:yyyy-MM-ddTHH:mm:ssZ}')) return {{ type:2 }};
	if(doc.StartTimeUtc >= ISODate('{0:yyyy-MM-ddTHH:mm:ssZ}')) return {{ type:1 }};			
	return {{ type:0 }};
}}", tomorrow, in2Day2, in7Day2));

                var countResult = Collection.Group(args);
                result.Counts = new int[4];
                foreach (var c in countResult) {
                    result.Counts[c.GetValue("type").ToInt32()] = c.GetValue("count").ToInt32();
                }
            }

            return result;
        }

        public void Join(string lobbyId, PuPUser user)
        {
            var lobby = GetById(lobbyId);
            var rejoinUser = lobby.Users.FirstOrDefault(x => x.Id == user.Id);
            if (rejoinUser == null)
            {
                //join;
                lobby.Users.Add(new LobbyUserInfo
                {
                    Id = user.Id,
                    PortraitUrl = user.PortraitUrl,
                    UserName = user.GetUserName(lobby.Platform)
                });

                NotificationHubHelper.SendLobbyStart(lobby, user);
            }
            else
            {
                //rejoin 
                rejoinUser.IsLeave = false;
            }

            _chatService.JoinRoom(lobby, new[] { user });
            Update(lobby);
        }

        public void Leave(string lobbyId, PuPUser user)
        {
            var lobby = GetById(lobbyId);
            var lobbyUser = lobby.Users.First(x => x.Id == user.Id);
            lobbyUser.IsLeave = true;

            if (lobbyUser.IsOwner)
            {
                // Change Ownership, choose the first one.
                var newOwner = lobby.Users.FirstOrDefault(x => !x.IsLeave && !x.IsOwner);
                if (newOwner == null)
                {
                    lobby.State = ModelState.Disactive;
                }
                else
                {
                    newOwner.IsOwner = true;
                }
            }

            lobbyUser.IsOwner = false;

            _chatService.LeaveRoom(lobby, new[] { user });
            Update(lobby);
        }

        protected override Expression<Func<Lobby, object>> GetOrderExpression(BaseFilter filter)
        {
            if (string.IsNullOrWhiteSpace(filter.Order))
            {
                return x => x.StartTimeUtc;
            }
            switch (filter.Order.ToLower())
            {
                case "name":
                    return x => x.Name;
                case "popular":
                    return x => x.Users.Count;
                case "starttime":
                    return x => x.StartTimeUtc;
                default:
                    return x => x.StartTimeUtc;
            }
        }
    }
}