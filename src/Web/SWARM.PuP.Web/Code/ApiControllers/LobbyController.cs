using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Formatting;
using System.Text;
using System.Web.Http;
using System.Web.Http.Filters;
using System.Web.Http.Results;
using MongoDB.Bson;
using MongoDB.Driver.Builders;
using SWARM.PuP.Web.Code;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/Lobby")]
    public class LobbyController : ApiController
    {
        private readonly IGameService _gameService;
        private readonly ILobbyService _lobbyService;

        public LobbyController(ILobbyService lobbyService, IGameService gameService)
        {
            _lobbyService = lobbyService;
            _gameService = gameService;
        }

        public LobbySearchResult Get([FromUri] LobbyFilter filter)
        {
            filter = filter ?? new LobbyFilter();

            if (!filter.StartTimeUtc.HasValue)
            {
                filter.StartTimeUtc = DateTime.UtcNow.AddMinutes(Consts.ShowTimeOffset);
            }

            return _lobbyService.Filter(filter);
        }

        [HttpGet, Route("Message/{lobbyId}")]
        public IHttpActionResult Message(string lobbyId)
        {
            //TODO: the just and only for Quicklbox.
            var lobby = _lobbyService.GetById(lobbyId);
            if (lobby == null)
            {
                throw new ArgumentException(ErrorCode.E003NotFoundLobby);
            }

            var url = string.Format("{0}?chat_dialog_id={1}&limit=19&sort_desc=date_sent", QuickbloxApiTypes.Message, lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId));
            var request = QuickbloxHttpHelper.Create(url, HttpMethod.Get);

            var result = request.ReadAll();
            
            int start = result.IndexOf("[", StringComparison.Ordinal);
            result = result.Substring(start, result.Length - start - 1);
            return ResponseMessage(new HttpResponseMessage(HttpStatusCode.OK) { Content = new StringContent(result, Encoding.UTF8, "text/json") });
        }

        [Authorize, Route("My")]
        public IEnumerable<Lobby> GetMy([FromUri] LobbyFilter filter)
        {
            //TODO: the just and only for Quicklbox.
            filter = filter ?? new LobbyFilter();

            PuPUser user = User.Identity.GetPuPUser();
            Session session = QuickbloxHttpHelper.InitSession(user.Id);

            string apiUrl = QuickbloxApiTypes.Room + string.Format("?limit={0}&skip={1}", filter.PageSize, filter.PageIndex * filter.PageSize);
            var request = QuickbloxHttpHelper.Create(apiUrl, HttpMethod.Get, session);

            var result = request.GetJson<QuickbloxRoomQueryResult>();

            if (result.items.Count == 0)
            {
                return new Lobby[0];
            }
            else
            {
                var charIds = result.items.Select(x => new BsonString(x._id));
                var lobbies = ((LobbyService)_lobbyService).Collection.FindAs<Lobby>(Query.In("Tags.Value", charIds)).ToArray();

                List<Lobby> sortedLobbies = new List<Lobby>();
                foreach (var room in result.items)
                {
                    var lobby = lobbies.FirstOrDefault(x => x.Tags.Any(y => y.Value == room._id));
                    if (lobby != null)
                    {
                        lobby.LastMessage = room.last_message;
                        lobby.LastMessageAt = Consts.JavaDateTime.AddSeconds(room.last_message_date_sent).ToUniversalTime();
                        lobby.UnreadMessageCount = room.unread_messages_count;

                        sortedLobbies.Add(lobby);
                    }
                }

                return sortedLobbies;
            }
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
                var medium = user.SocialMedia.FirstOrDefault(x => x.Type == type);
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