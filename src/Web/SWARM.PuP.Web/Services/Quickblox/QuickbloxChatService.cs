using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Claims;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxChatService : IChatService
    {
        private const string LobbyNameFormat = "Lobby:{0}";
        private const string StringChatRoomId = "ChatRoomId";

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
            var request = QuickbloxHttpHelper.Create(string.Format(QuickbloxApiTypes.UserDelete, chatId), HttpMethod.Delete);

            request.GetResponse();
        }

        public void CreateRoomForLobby(Lobby lobby)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Room, HttpMethod.Post);

            var charRoom = request.Json<QuickbloxRoom>(new
            {
                type = ChatRoomType.Group,
                name = string.Format(LobbyNameFormat, lobby.Name)
            });

            lobby.UpdateTag(StringChatRoomId, charRoom._id);
        }

        public void SendMessage(Lobby lobby, string message)
        {
            var roomId = lobby.GetTagValue(StringChatRoomId);
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Message, HttpMethod.Post);

            request.Json<QuickbloxMessage>(new
            {
                chat_dialog_id = roomId,
                message
            });
        }
    }
}