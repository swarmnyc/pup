using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Text;
using System.Web.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Web;
using SWARM.PuP.Web;
using SWARM.PuP.Web.Controllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass]
    public class QuickbloxChatServiceTest
    {
        public QuickbloxChatServiceTest()
        {
            TestHelper.MockDatabase();
        }

        [TestMethod]
        public void Real_QuickbloxChatService_CreateUser_Test()
        {
            try
            {
                QuickbloxChatService s = new QuickbloxChatService(new UserService());
                var puPUser = new PuPUser()
                {
                    UserName = "Test",
                    Email = "Test@swarmnyc.com"
                };
                s.CreateUser(puPUser);

                //Assert.IsNull(puPUser.ChatId);
            }
            catch (WebException e)
            {
                var error = e.Response.ReadAll();
                Assert.Fail(error);
            }
        }

        [TestMethod]
        public void Real_QuickbloxChatService_CreateRoom_Test()
        {
            try
            {
                var userService = new UserService();
                QuickbloxChatService s = new QuickbloxChatService(new UserService());
                s.CreateRoomForLobby(new Lobby()
                {
                    Name = "Test-" + DateTime.Now.ToString(CultureInfo.CurrentCulture),
                    UserIds = new HashSet<string>(userService.All().Take(1).Select(x => x.Id))
                });

            }
            catch (WebException e)
            {
                var error = e.Response.ReadAll();
                Assert.Fail(error);
            }
        }

        [TestMethod]

        public void Real_QuickbloxChatService_JoinRoom_Test()
        {
            try
            {
                var userService = new UserService();
                QuickbloxChatService s = new QuickbloxChatService(new UserService());
                Lobby lobby = new Lobby();
                lobby.AddTag(QuickbloxHttpHelper.Const_ChatRoomId, "5515c344535c120576011113");

                s.JoinRoom(lobby, userService.All().Skip(1).Select(x => x.Id));

            }
            catch (WebException e)
            {
                var error = e.Response.ReadAll();
                Assert.Fail(error);
            }
        }

        [TestMethod]

        public void Real_QuickbloxChatService_LeaveRoom_Test()
        {
            try
            {
                var userService = new UserService();
                QuickbloxChatService s = new QuickbloxChatService(new UserService());
                Lobby lobby = new Lobby();
                lobby.AddTag(QuickbloxHttpHelper.Const_ChatRoomId, "5515c344535c120576011113");

                s.LeaveRoom(lobby, userService.All().Skip(1).Select(x => x.Id));

            }
            catch (WebException e)
            {
                var error = e.Response.ReadAll();
                Assert.Fail(error);
            }
        }

        [TestMethod]
        public void Real_QuickbloxChatService_SendMessage_Test()
        {
            try
            {
                QuickbloxChatService s = new QuickbloxChatService(new UserService());

                s.SendMessage(new Lobby()
                {
                    Tags = new List<PuPTag>()
                    {
                        new PuPTag(QuickbloxHttpHelper.Const_ChatRoomId, "550cd690535c12ad7c0046aa")
                    }
                }, "Yayaya!!");
            }
            catch (WebException e)
            {
                var error = e.Response.ReadAll();
                Assert.Fail(error);
            }
        }
    }
}
