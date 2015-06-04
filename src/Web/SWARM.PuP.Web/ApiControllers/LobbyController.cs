using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web;
using System.Web.Http;
using System.Web.Http.Filters;
using System.Web.Mvc;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.ApiControllers
{
    [System.Web.Http.RoutePrefix("api/Lobby")]
    public class LobbyController : ApiController
    {
        private const int ShowTimeOffset = -15;

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
            filter = filter ?? new LobbyFilter();

            if (!filter.StartTimeUtc.HasValue)
            {
                filter.StartTimeUtc = DateTime.UtcNow.AddMinutes(ShowTimeOffset);
            }

            return LobbyViewModel.Load(_lobbyService.Filter(filter));
        }

        [System.Web.Http.Authorize, System.Web.Http.Route("My")]
        public IEnumerable<LobbyViewModel> GetMy([FromUri] LobbyFilter filter)
        {
            filter = filter ?? new LobbyFilter();
            filter.UserId = User.Identity.GetPuPUser().Id;
            filter.OrderDirection = ListSortDirection.Descending;
            return LobbyViewModel.Load(_lobbyService.Filter(filter));
        }

        [ModelValidate]
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

        [System.Web.Http.Authorize, ModelValidate]
        public Lobby Post(Lobby lobby)
        {
            var game = _gameService.GetById(lobby.GameId);
            if (string.IsNullOrWhiteSpace(lobby.Name))
            {
                lobby.Name = game.Name;
            }

            lobby.PictureUrl = game.PictureUrl;
            lobby.ThumbnailPictureUrl = game.ThumbnailPictureUrl;

            return _lobbyService.Add(lobby, User.Identity.GetPuPUser());
        }

        [System.Web.Http.Authorize, ModelValidate]
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

        [System.Web.Http.Authorize, System.Web.Http.Route("Join/{lobbyId}"), System.Web.Http.HttpPost, ModelValidate]
        public IHttpActionResult Join(string lobbyId)
        {
            _lobbyService.Join(lobbyId, User.Identity.GetPuPUser());

            return Ok();
        }

        [System.Web.Http.Authorize, System.Web.Http.Route("Leave/{lobbyId}"), System.Web.Http.HttpPost, ModelValidate]
        public IHttpActionResult Leave(string lobbyId)
        {
            _lobbyService.Leave(lobbyId, User.Identity.GetPuPUser());

            return Ok();
        }

        [System.Web.Http.Authorize, System.Web.Http.Route("Invite/{lobbyId}"), System.Web.Http.HttpPost, ModelValidate]
        public IHttpActionResult Invite(string lobbyId)
        {
            var user = User.Identity.GetPuPUser();
            var lobby = _lobbyService.GetById(lobbyId);

            foreach (var medium in user.Media)
            {
                switch (medium.Type)
                {
                    case "Facebook":
                        ShareToFb(medium, lobby);
                        break;
                }
            }

            return Ok();
        }

        private void ShareToFb(SocialMedium medium, Lobby lobby)
        {
            string msg = HttpUtility.UrlEncode("Let's play " + lobby.Name + " with me.");
            string link = HttpUtility.UrlEncode(Url.Content("~/lobby/" + lobby.Id));
            var url = string.Format("https://graph.facebook.com/v2.3/me/feed?access_token={0}&message={1}&link={2}", medium.Token, msg, link);
            WebRequest webRequest = WebRequest.CreateHttp(url);
            webRequest.Method = "POST";
            var response = webRequest.GetResponse();
            response.GetResponseStream();
        }
    }
}