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
        private const int ShowTimeOffset = -15;
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
                PictureUrl = owner.PictureUrl,
                Name = owner.GetUserName(lobby.Platform)
            });

            _chatService.CreateRoomForLobby(owner, lobby);

            return base.Add(lobby);
        }

        public IQueryable<Lobby> Filter(LobbyFilter filter)
        {
            var query = All();

            filter = filter ?? new LobbyFilter();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(x => x.Name.ToLower().Contains(filter.Search));
            }

            if (!string.IsNullOrWhiteSpace(filter.GameId))
            {
                query = query.Where(x => x.GameId == filter.GameId);
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

            if (!filter.StartTimeUtc.HasValue)
            {
                filter.StartTimeUtc = DateTime.UtcNow.AddMinutes(ShowTimeOffset);
            }

            query = query.Where(x => x.StartTimeUtc >= filter.StartTimeUtc);
            query = query.Where(x => x.State == ModelState.Actived);

            query = DoOrderQuery(query, filter);

            return query;
        }

        public void Join(string lobbyId, PuPUser user)
        {
            var lobby = GetById(lobbyId);
            lobby.Users.Add(new LobbyUserInfo
            {
                Id = user.Id,
                PictureUrl = user.PictureUrl,
                Name = user.GetUserName(lobby.Platform)
            });

            _chatService.JoinRoom(lobby, new[] {user});
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
                    lobby.State = ModelState.Disactived;
                }
                else
                {
                    newOwner.IsOwner = true;
                }
            }

            lobbyUser.IsOwner = false;

            _chatService.LeaveRoom(lobby, new[] {user});
            Update(lobby);
        }

        protected override Expression<Func<Lobby, object>> GetOrderExpression(BaseFilter filter)
        {
            if (string.IsNullOrWhiteSpace(filter.Order))
            {
                return (Lobby x) => x.StartTimeUtc;
            }
            switch (filter.Order.ToLower())
            {
                case "name":
                    return (Lobby x) => x.Name;
                case "popular":
                    return (Lobby x) => x.Users.Count;
                case "starttime":
                default:
                    return (Lobby x) => x.StartTimeUtc;
            }
        }
    }
}