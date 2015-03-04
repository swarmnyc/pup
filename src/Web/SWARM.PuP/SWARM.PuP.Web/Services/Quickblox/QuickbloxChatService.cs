using System;
using System.Net;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxChatService : IChatService
    {
        public void CreateUser(PuPUser user)
        {
            var request = QuickbloxHttpHelper.Create(QuickbloxRequestTypes.CreateUser, "POST");
            request.Write(new
            {
                user = new
                {
                    login = user.UserName,
                    password = "swarmnyc",
                    email = user.Email
                }
            });

            var result = request.GetResponse().Read<CreateUserResult>();

            user.ChatId = result.user.id.ToString();
        }
    }
}