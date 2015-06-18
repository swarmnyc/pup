using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxChatService : IChatService
    {
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
            var chatId = user.GetChatId();
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.UserDelete(chatId), HttpMethod.Delete);

            request.GetResponse();
        }

        public void CreateRoomForLobby(PuPUser owner, Lobby lobby)
        {
            string[] chatUsersId = { owner.GetChatId() };
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Room, HttpMethod.Post);

            var charRoom = request.Json<QuickbloxRoom>(new
            {
                type = ChatRoomType.Group,
                name = string.Format(QuickbloxHttpHelper.LobbyNameFormat, lobby.Name),
                occupants_ids = string.Join(",", chatUsersId)
            });

            lobby.UpdateTag(QuickbloxHttpHelper.Const_ChatRoomId, charRoom._id);
        }

        public void JoinRoom(Lobby lobby, IEnumerable<PuPUser> users)
        {
            var chatUsersId = users.Select(x => x.GetChatId()).ToArray();

            var request =
                QuickbloxHttpHelper.Create(
                    QuickbloxApiTypes.RoomUpdate(lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId)),
                    HttpMethod.Put);

            var chatRoom = request.Json<QuickbloxRoom>(new
            {
                //push_all = add users
                push_all = new
                {
                    occupants_ids = chatUsersId
                }
            });

            SendSystemMessage(lobby, SystemMessageCode.Join, users);
        }

        public void LeaveRoom(Lobby lobby, IEnumerable<PuPUser> users)
        {
            var chatUsersId = users.Select(x => x.GetChatId()).ToArray();

            var request =
                QuickbloxHttpHelper.Create(
                    QuickbloxApiTypes.RoomUpdate(lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId)),
                    HttpMethod.Put);

            var chatRoom = request.Json<QuickbloxRoom>(new
            {
                //pull_all = remove users
                pull_all = new
                {
                    occupants_ids = chatUsersId
                }
            });

            SendSystemMessage(lobby, SystemMessageCode.Leave, users);

        }

        public void SendSystemMessage(Lobby lobby, SystemMessageCode code, IEnumerable<PuPUser> users)
        {
            var roomId = lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId);
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Message, HttpMethod.Post);
            string message;
            string codeBody;
            switch (code)
            {
                case SystemMessageCode.Join:
                    message = string.Format("{0} joined this lobby", string.Join(", ", users.Select(x => x.UserName).ToArray()));
                    codeBody = users.Select(x => new { x.Id, x.UserName, x.PortraitUrl }).ToJson();
                    break;
                case SystemMessageCode.Leave:
                    message = string.Format("{0} left this lobby", string.Join(", ", users.Select(x => x.UserName).ToArray()));
                    codeBody = users.Select(x => new { x.Id, x.UserName, x.PortraitUrl }).ToJson();
                    break;
                default:
                    throw new ArgumentOutOfRangeException("code", code, null);
            }

            request.Json<QuickbloxMessage>(new QuickbloxMessage
            {
                chat_dialog_id = roomId,
                code = code.ToString(),
                codeBody = codeBody,
                message = message
            });
        }
    }

    public enum SystemMessageCode
    {
        Join,
        Leave
    }
}