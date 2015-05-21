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
        public void CreateUser(PuPUser user)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.User, HttpMethod.Post);

            string username = Guid.NewGuid().ToString();
            var result = request.Json<CreateUserResult>(new
            {
                user = new
                {
                    login = username,
                    password = QuickbloxHttpHelper.UserPassword,
                    email = username + QuickbloxHttpHelper.CompanyEmailDomin
                }
            });

            user.SetChatId(result.user.id.ToString(), username);
        }

        public void DeleteUser(PuPUser user)
        {
            string chatId = user.GetChatId();
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.UserDelete(chatId), HttpMethod.Delete);

            request.GetResponse();
        }

        public void CreateRoomForLobby(PuPUser owner, Lobby lobby)
        {
            string[] chatUsersId = new string[] { owner.GetChatId() };
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Room, HttpMethod.Post);

            var charRoom = request.Json<QuickbloxRoom>(new
            {
                type = ChatRoomType.Group,
                name = string.Format(QuickbloxHttpHelper.LobbyNameFormat, lobby.Name),
                occupants_ids = String.Join(",", chatUsersId)
            });

            lobby.UpdateTag(QuickbloxHttpHelper.Const_ChatRoomId, charRoom._id);
        }

        public void JoinRoom(Lobby lobby, IEnumerable<PuPUser> users)
        {
            string[] chatUsersId = users.Select(x => x.GetChatId()).ToArray();

            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.RoomUpdate(lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId)), HttpMethod.Put);

            var charRoom = request.Json<QuickbloxRoom>(new
            {
                push_all = new
                {
                    occupants_ids = chatUsersId
                }
            });
        }

        public void LeaveRoom(Lobby lobby, IEnumerable<PuPUser> users)
        {
            string[] chatUsersId = users.Select(x => x.GetChatId()).ToArray();

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
    }
}