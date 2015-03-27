using System.Collections.Generic;
using System.Web.Http;
using Microsoft.AspNet.Identity;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/Lobby")]
    public class LobbyController : ApiController
    {
        private readonly ILobbyService _lobbyService;

        public LobbyController(ILobbyService lobbyService)
        {
            _lobbyService = lobbyService;
        }

        public IEnumerable<Lobby> Get([FromUri]LobbyFilter filter)
        {
            return _lobbyService.Filter(filter);
        }

        public Lobby Get(string id)
        {
            return _lobbyService.GetById(id);
        }

        [Authorize]
        public Lobby Post(Lobby lobby)
        {
            lobby.UserIds.Add(User.Identity.GetUserId());
            return _lobbyService.Add(lobby);
        }

        [Authorize]
        public void Put(Lobby lobby)
        {
            var origin = _lobbyService.GetById(lobby.Id);
            origin.PlayStyle = lobby.PlayStyle;
            origin.SkillLevel = lobby.SkillLevel;
            origin.StartTimeUtc = lobby.StartTimeUtc;
            origin.Tags = lobby.Tags;

            _lobbyService.Update(lobby);
        }

        [Authorize, Route("Join/{lobbyId}")]
        public IHttpActionResult Join(string lobbyId)
        {   
            _lobbyService.Join(lobbyId, User.Identity.GetUserId());

            return Ok();
        }

        [Authorize, Route("Leave/{lobbyId}")]
        public IHttpActionResult Leave(string lobbyId)
        {
            _lobbyService.Leave(lobbyId, User.Identity.GetUserId());

            return Ok();
        }
    }
}