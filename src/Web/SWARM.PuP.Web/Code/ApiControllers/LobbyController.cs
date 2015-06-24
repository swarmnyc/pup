using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Web.Http;
using System.Web.Http.Filters;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;

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
            if (lobby == null)
            {
                throw new ArgumentException(ErrorCode.E003NotFoundLobby);
            }

            return lobby;
        }

        [Authorize, ModelValidate]
        public Lobby Post(Lobby lobby)
        {
            var game = _gameService.GetById(lobby.GameId);
            if (game == null)
            {
                throw new ArgumentException(ErrorCode.E003NotFoundGame);
            }

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
        public IHttpActionResult Invite(string lobbyId, [FromUri] string localTime,
            [FromUri] IEnumerable<SocialMediumType> types)
        {
            var user = User.Identity.GetPuPUser();
            var lobby = _lobbyService.GetById(lobbyId);

            if (user == null)
            {
                throw new ArgumentException(ErrorCode.E003NotFoundUser);
            }

            if (lobby == null)
            {
                throw new ArgumentException(ErrorCode.E003NotFoundLobby);
            }

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
                        ShareHelper.ShareToFacebook(medium, lobby, localTime);
                        break;
                    case SocialMediumType.Twitter:
                        ShareHelper.ShareToTwitter(medium, lobby, localTime);
                        break;
                    case SocialMediumType.Tumblr:
                        ShareHelper.ShareToTumblur(medium, lobby, localTime);
                        break;
                }
            }

            return Ok();
        }
    }
}