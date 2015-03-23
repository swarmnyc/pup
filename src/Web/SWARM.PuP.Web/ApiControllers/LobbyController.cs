using System.Collections.Generic;
using System.Web.Http;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.ApiControllers
{
    public class LobbyController : ApiController
    {
        private readonly ILobbyService _lobbyService;

        public LobbyController(ILobbyService lobbyService)
        {
            _lobbyService = lobbyService;
        }

        // GET: api/Lobby
        public IEnumerable<Lobby> Get([FromUri]LobbyFilter filter)
        {
            return _lobbyService.Filter(filter);
        }

        // GET: api/Lobby/5
        public Lobby Get(string id)
        {
            return _lobbyService.GetById(id);
        }

        // POST: api/Lobby
        [Authorize]
        public void Post(Lobby lobby)
        {
            _lobbyService.Add(lobby);
        }

        // PUT: api/Lobby/5
        [Authorize]
        public void Put(Lobby lobby)
        {
            _lobbyService.Update(lobby);
        }
    }
}