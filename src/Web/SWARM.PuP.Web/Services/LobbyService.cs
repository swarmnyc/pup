using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using Microsoft.AspNet.Identity;
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

        public Lobby Add(PuPUser owner, Lobby lobby)
        {
            lobby.Users.Add(new LobbyUserInfo()
            {
                Id = owner.Id,
                IsOwner = true,
                PictureUrl = owner.PictureUrl,
                Name = owner.GetUserName(lobby.Platforms.First())
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

        public void Join(string lobbyId, PuPUser user)
        {
            Lobby lobby = this.GetById(lobbyId);
            lobby.Users.Add(new LobbyUserInfo()
            {
                Id = user.Id,
                PictureUrl = user.PictureUrl,
                Name = user.GetUserName(lobby.Platforms.First())
            });

            _chatService.JoinRoom(lobby, new PuPUser[] { user });
            Update(lobby);
        }

        public void Leave(string lobbyId, PuPUser user)
        {
            Lobby lobby = this.GetById(lobbyId);
            var lobbyUser = lobby.Users.First(x => x.Id == user.Id);
            lobbyUser.IsLeave = true;

            _chatService.LeaveRoom(lobby, new PuPUser[] { user });
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