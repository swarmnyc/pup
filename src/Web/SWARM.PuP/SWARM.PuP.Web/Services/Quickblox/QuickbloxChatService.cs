using System;
using System.Net;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxChatService : IChatService
    {
        public void CreateUser(PuPUser user)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.User, "POST");

            var result = request.Json<CreateUserResult>(new
            {
                user = new
                {
                    login = user.UserName,
                    password = "swarmnyc",
                    email = user.Email
                }
            }); 

            user.ChatId = result.user.id.ToString();
        }

        public string CreateRoom(ChatRoomType type, string roomName)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Room, "POST");

            return request.Json<QuickbloxRoom>(new
            {
                type = (int)type,
                name = roomName
            })._id;
        }

        public void SendMessage(string roomId, string message)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxApiTypes.Message, "POST");

            request.Json<QuickbloxMessage>(new
            {
                chat_dialog_id = roomId,
                message
            });
        }
    }
}