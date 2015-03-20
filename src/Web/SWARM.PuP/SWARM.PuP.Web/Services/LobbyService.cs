using System;
using System.Collections.Generic;
using System.Linq;
using MongoDB;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public class LobbyService : MongoService<Lobby>, ILobbyService
    {
        private readonly IChatService _chatService;

        public LobbyService(IChatService chatService) : base("Lobbies")
        {
            _chatService = chatService;
        }

        public override Lobby Add(Lobby lobby)
        {
            lobby.ChatRoomId = _chatService.CreateRoom(ChatRoomType.Public, "Lobby:" + lobby.Name);
            return base.Add(lobby);
        }

        public override IEnumerable<Lobby> Add(IEnumerable<Lobby> lobbies)
        {
            foreach (var lobby in lobbies)
            {
                //TODO: Check it needs user;
                lobby.ChatRoomId = _chatService.CreateRoom(ChatRoomType.Public, "Lobby:" + lobby.Name);
            }

            return base.Add(lobbies);
        }

        public IQueryable<Lobby> Filter(LobbyFilter filter)
        {
            var query = this.All();
            
            filter = filter ?? new LobbyFilter();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(x => x.Name.ToLower().Contains(filter.Search));
            }

            if (!filter.Platforms.IsNullOrEmpty())
            {
                query = query.Where(x => x.Platforms.ContainsAny(filter.Platforms));
            }

            query = filter.DoOrderQuery(query);

            return query;
        }
    }
}