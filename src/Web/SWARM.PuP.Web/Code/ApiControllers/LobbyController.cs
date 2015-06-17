using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Text;
using System.Web;
using System.Web.Http;
using System.Web.Http.Filters;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using TwitterOAuth.Enum;
using TwitterOAuth.Impl;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/Lobby")]
    public class LobbyController : ApiController
    {
        private const int ShowTimeOffset = -15;
        private readonly IGameService _gameService;
        private readonly ILobbyService _lobbyService;

        public LobbyController(ILobbyService lobbyService, IGameService gameService)
        {
            _lobbyService = lobbyService;
            _gameService = gameService;
        }

        public IEnumerable<Lobby> Get([FromUri] LobbyFilter filter)
        {
            filter = filter ?? new LobbyFilter();

            if (!filter.StartTimeUtc.HasValue)
            {
                filter.StartTimeUtc = DateTime.UtcNow.AddMinutes(ShowTimeOffset);
            }

            return _lobbyService.Filter(filter);
        }

        [Authorize, Route("My")]
        public IEnumerable<Lobby> GetMy([FromUri] LobbyFilter filter)
        {
            filter = filter ?? new LobbyFilter();
            filter.UserId = User.Identity.GetPuPUser().Id;
            filter.OrderDirection = ListSortDirection.Descending;
            return _lobbyService.Filter(filter);
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

        [Authorize, ModelValidate]
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

        [Authorize, ModelValidate]
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

        [Authorize, Route("Join/{lobbyId}"), HttpPost, ModelValidate]
        public IHttpActionResult Join(string lobbyId)
        {
            _lobbyService.Join(lobbyId, User.Identity.GetPuPUser());

            return Ok();
        }

        [Authorize, Route("Leave/{lobbyId}"), HttpPost, ModelValidate]
        public IHttpActionResult Leave(string lobbyId)
        {
            _lobbyService.Leave(lobbyId, User.Identity.GetPuPUser());

            return Ok();
        }

        [Authorize, Route("Invite/{lobbyId}"), HttpPost, ModelValidate]
        public IHttpActionResult Invite(string lobbyId, [FromUri] string localTime, [FromUri] IEnumerable<SocialMediumType> types)
        {
            var user = User.Identity.GetPuPUser();
            var lobby = _lobbyService.GetById(lobbyId);

            foreach (var type in types)
            {
                var medium = user.Media.FirstOrDefault(x => x.Type == type);
                if (medium == null)
                {
                    continue;
                }

                switch (type)
                {
                    case SocialMediumType.Facebook:
                        ShareToFb(medium, lobby, localTime);
                        break;
                    case SocialMediumType.Twitter:
                        ShareToTwitter(medium, lobby, localTime);
                        break;
                }
            }

            return Ok();
        }

        private void ShareToTwitter(SocialMedium medium, Lobby lobby, string localTime)
        {
            try
            {
                //TODO: Move to Config, Bug with Chinese letter in AuthRequest
                var msg = HttpUtility.UrlEncode(GetMessage(lobby, localTime), Encoding.UTF8);
                var authRequest = new TwitterOAuthClient
                {
                    ConsumerKey = "tuPdqGWSqvDNRC8TrcJ1dyuSd",
                    ConsumerSecret = "oAjcN1hXSo0AZw9XauXU6qbwcR5FDBYnAvSHygFKbE2wg9kcxs",
                    Token = medium.Token,
                    TokenSecret = medium.Secret
                };

                authRequest.OAuthWebRequest(RequestMethod.POST, "https://api.twitter.com/1.1/statuses/update.json",
                    "status=" + msg);
            }
            catch (Exception ex)
            {
                Trace.TraceError("{0}\n{1}", ex.Message, ex.StackTrace);
            }
        }

        private void ShareToFb(SocialMedium medium, Lobby lobby, string localTime)
        {
            try
            {
                var msg = HttpUtility.UrlEncode(GetMessage(lobby, localTime), Encoding.UTF8);
                var link = HttpUtility.UrlEncode(Url.Content("~/lobby/" + lobby.Id));
                var url = string.Format("https://graph.facebook.com/v2.3/me/feed?access_token={0}&message={1}&link={2}",
                    medium.Token, msg, link);
                WebRequest webRequest = WebRequest.CreateHttp(url);
                webRequest.Method = "POST";
                var response = webRequest.GetResponse();
                response.GetResponseStream();
            }
            catch (Exception ex)
            {
                Trace.TraceError("{0}\n{1}", ex.Message, ex.StackTrace);
            }

        }

        private static string GetMessage(Lobby lobby, string localTime)
        {
            return string.Format(@"Who's up for {0} this {1}

PUP: Find gamers to play with http://partyupplayer.com", lobby.Name, localTime);
        }
    }
}