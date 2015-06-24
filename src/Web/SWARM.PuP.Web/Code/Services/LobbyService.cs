using System;
using System.Linq;
using System.Linq.Expressions;
using MongoDB;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public class LobbyService : BaseService<Lobby>, ILobbyService
    {
        private readonly IChatService _chatService;

        public LobbyService(IChatService chatService) : base("Lobbies")
        {
            _chatService = chatService;
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

            return base.Add(lobby);
        }

        public IQueryable<Lobby> Filter(LobbyFilter filter)
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

            return query;
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