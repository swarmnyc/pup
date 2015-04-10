using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using System.Web.Http.ExceptionHandling;
using System.Web.Http.Results;
using Microsoft.AspNet.Identity;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.ViewModels;

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

        public IEnumerable<LobbyViewModel> Get([FromUri]LobbyFilter filter)
        {
            return LobbyViewModel.Load(_lobbyService.Filter(filter));
        }

        public Lobby Get(string id)
        {
            return _lobbyService.GetById(id);
        }

        [Authorize]
        public Lobby Post(Lobby lobby)
        {
            return _lobbyService.Add(lobby, _userService.Get(User.Identity));
        }

        [Authorize]
        public IHttpActionResult Put(Lobby lobby)
        {
            var origin = _lobbyService.GetById(lobby.Id);
            origin.PlayStyle = lobby.PlayStyle;
            origin.SkillLevel = lobby.SkillLevel;
            origin.StartTimeUtc = lobby.StartTimeUtc;
            //origin.Tags = lobby.Tags;

            _lobbyService.Update(lobby);

            return Ok();
        }

        [Authorize, Route("Join/{lobbyId}"), HttpPost]
        public IHttpActionResult Join(string lobbyId)
        {   
            _lobbyService.Join(lobbyId, _userService.Get(User.Identity));

            return Ok();
        }

        [Authorize, Route("Leave/{lobbyId}"), HttpPost]
        public IHttpActionResult Leave(string lobbyId)
        {
            _lobbyService.Leave(lobbyId, _userService.Get(User.Identity));

            return Ok();
        }
    }
}