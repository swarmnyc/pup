using System;
using System.Collections.Generic;
using System.Linq;
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
        private readonly IUserService _userService;
        private readonly ILobbyService _lobbyService;

        public LobbyController(IUserService userService, ILobbyService lobbyService)
        {
            _userService = userService;
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
            return _lobbyService.Add(_userService.Get(User.Identity), lobby);
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
            _lobbyService.Join(lobbyId, _userService.Get(User.Identity));

            return Ok();
        }

        [Authorize, Route("Leave/{lobbyId}")]
        public IHttpActionResult Leave(string lobbyId)
        {
            _lobbyService.Leave(lobbyId, _userService.Get(User.Identity));

            return Ok();
        }
    }
}