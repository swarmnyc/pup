using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
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

        public override Lobby Add(Lobby lobby)
        {
            _chatService.CreateRoomForLobby(lobby);
            return base.Add(lobby);
        }

        public override IEnumerable<Lobby> Add(IEnumerable<Lobby> lobbies)
        {
            foreach (var lobby in lobbies)
            {
                _chatService.CreateRoomForLobby(lobby);
            }

            return base.Add(lobbies);
        }

        public IQueryable<Lobby> Filter(LobbyFilter filter)
        {
            var query = All();

            filter = filter ?? new LobbyFilter();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(x => x.Name.ToLower().Contains(filter.Search));
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
                query = query.Where(x => x.Platforms.ContainsAny(filter.Platforms));
            }

            query = DoOrderQuery(query, filter);

            return query;
        }

        public void Join(string lobbyId, string userId)
        {
            Lobby lobby = this.GetById(lobbyId);
            lobby.UserIds.Add(userId);

            _chatService.JoinRoom(lobby, new String[] { userId });
            Update(lobby);
        }

        public void Leave(string lobbyId, string userId)
        {
            Lobby lobby = this.GetById(lobbyId);
            lobby.UserIds.Remove(userId);

            _chatService.LeaveRoom(lobby, new String[] { userId });
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
                    return (Lobby x) => x.UserIds.Count;
                case "starttime":
                default:
                    return (Lobby x) => x.StartTimeUtc;
            }
        }
    }
}