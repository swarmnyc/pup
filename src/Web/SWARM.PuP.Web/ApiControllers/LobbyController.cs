using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Web;
using System.Web.Http;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/Lobby")]
    public class LobbyController : ApiController
    {
        private readonly IGameService _gameService;
        private readonly ILobbyService _lobbyService;
        private readonly IUserService _userService;

        public LobbyController(IUserService userService, ILobbyService lobbyService, IGameService gameService)
        {
            _userService = userService;
            _lobbyService = lobbyService;
            _gameService = gameService;
        }

        public IEnumerable<LobbyViewModel> Get([FromUri] LobbyFilter filter)
        {
            return LobbyViewModel.Load(_lobbyService.Filter(filter));
        }

        public Lobby Get(string id)
        {
            var lobby = _lobbyService.GetById(id);
            foreach (var user in lobby.Users)
            {
                if (user.PortraitUrl.IsNotNullOrWhiteSpace())
                {
                    user.PortraitUrl = Url.Content(user.PortraitUrl);
                }
            }

            return lobby;
        }

        [Authorize]
        public Lobby Post(Lobby lobby)
        {
            var game = _gameService.GetById(lobby.GameId);
            if (String.IsNullOrWhiteSpace(lobby.Name))
            {
                lobby.Name = game.Name;
            }

            lobby.PictureUrl = game.PictureUrl;
            lobby.ThumbnailPictureUrl = game.ThumbnailPictureUrl;

            return _lobbyService.Add(lobby, User.Identity.GetPuPUser());
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
            _lobbyService.Join(lobbyId, User.Identity.GetPuPUser());

            return Ok();
        }

        [Authorize, Route("Leave/{lobbyId}"), HttpPost]
        public IHttpActionResult Leave(string lobbyId)
        {
            _lobbyService.Leave(lobbyId, User.Identity.GetPuPUser());

            return Ok();
        }
    }
}