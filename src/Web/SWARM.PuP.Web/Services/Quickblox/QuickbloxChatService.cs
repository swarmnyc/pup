using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Claims;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxChatService : IChatService
    {
        private readonly IUserService _userService;

        public QuickbloxChatService(IUserService userService)
        {
            _userService = userService;
        }

        public void CreateUser(PuPUser user)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.User, HttpMethod.Post);

            var result = request.Json<CreateUserResult>(new
            {
                user = new
                {
                    login = user.Id,
                    password = QuickbloxHttpHelper.UserPassword,
                    email = user.Id + QuickbloxHttpHelper.CompanyEmailDomin
                }
            });

            user.SetChatId(result.user.id.ToString());
        }

        public void DeleteUser(PuPUser user)
        {
            string chatId = user.GetChatId();
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.UserDelete(chatId), HttpMethod.Delete);

            request.GetResponse();
        }

        public void CreateRoomForLobby(Lobby lobby)
        {
            string[] chatUsersId = ConvertUser(lobby.UserIds);
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Room, HttpMethod.Post);

            var charRoom = request.Json<QuickbloxRoom>(new
            {
                type = ChatRoomType.Group,
                name = string.Format(QuickbloxHttpHelper.LobbyNameFormat, lobby.Name),
                occupants_ids = String.Join(",", chatUsersId)
            });

            lobby.UpdateTag(QuickbloxHttpHelper.Const_ChatRoomId, charRoom._id);
        }

        public void JoinRoom(Lobby lobby, IEnumerable<string> users)
        {
            string[] chatUsersId = ConvertUser(users);

            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.RoomUpdate(lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId)), HttpMethod.Put);

            var charRoom = request.Json<QuickbloxRoom>(new
            {
                push_all = new
                {
                    occupants_ids = chatUsersId
                }
            });
        }

        public void LeaveRoom(Lobby lobby, IEnumerable<string> users)
        {
            string[] chatUsersId = ConvertUser(users);

            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.RoomUpdate(lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId)), HttpMethod.Put);

            var charRoom = request.Json<QuickbloxRoom>(new
            {
                pull_all = new
                {
                    occupants_ids = chatUsersId
                }
            });
        }

        public void SendMessage(Lobby lobby, string message)
        {
            var roomId = lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId);
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Message, HttpMethod.Post);

            request.Json<QuickbloxMessage>(new
            {
                chat_dialog_id = roomId,
                message
            });
        }

        private string[] ConvertUser(IEnumerable<string> userIds)
        {
            return _userService.Get(x => x.Id.In(userIds)).Select(x => x.GetChatId()).ToArray();
        }
    }
}