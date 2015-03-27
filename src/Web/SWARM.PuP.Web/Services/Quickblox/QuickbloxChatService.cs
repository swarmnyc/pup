using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Claims;
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

            user.Tags.Add(new UserTag(UserTagType.Application, "ChatId", result.user.id.ToString()));
        }

        public void DeleteUser(PuPUser user)
        {
            string chatId = user.Tags.First(x => x.Key == "ChatId").Value;
            var request = QuickbloxHttpHelper.Create(string.Format(QuickbloxApiTypes.UserDelete, chatId), HttpMethod.Delete);

            request.GetResponse();
        }

        public string CreateRoom(ChatRoomType type, string roomName)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Room, HttpMethod.Post);

            return request.Json<QuickbloxRoom>(new
            {
                //type = type == ChatRoomType.Public ? 2 : 3,
                type = (int)type,
                name = roomName
            })._id;
        }

        public void SendMessage(string roomId, string message)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Message, HttpMethod.Post);

            request.Json<QuickbloxMessage>(new
            {
                chat_dialog_id = roomId,
                message
            });
        }
    }
}