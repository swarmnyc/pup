﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.UI.WebControls;
using Microsoft.AspNet.Identity;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass()]
    public class LobbyServiceTests
    {
        public LobbyServiceTests()
        {
            TestHelper.MockDatabase();

        }

        [TestMethod()]
        public void LobbyService_AddLobby_Test()
        {
            var chatService = new Mock<IChatService>();
            chatService.Setup(x => x.CreateRoomForLobby(It.IsAny<PuPUser>(), It.IsAny<Lobby>())).Callback(new Action<PuPUser, Lobby>((x, y) =>
              {
                  y.AddTag(QuickbloxHttpHelper.Const_ChatRoomId, "Test");
              }));

            var userService = new UserService();
            var service = new LobbyService(chatService.Object);
            var lobby = service.Add(userService.Collection.FindOne(), new Lobby()
            {
                GameId = "test",
                Name = "Test 2",
                PlayStyle = PlayStyle.Serious,
                Platforms = new List<GamePlatform>() { GamePlatform.Xbox },
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Pro,
                Description = "Test"
            });

            Assert.IsNotNull(lobby);
            Assert.IsNotNull(lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId));
        }

        [TestMethod()]
        public void LobbyService_Leave_Test()
        {
            var chatService = new Mock<IChatService>();

            var service = new LobbyService(chatService.Object);
            var lobbyId = service.Collection.FindOne().Id;
            var userService = new UserService();
            service.Join(lobbyId, userService.GetSingle(x => x.DisplayName == "test"));
            service.Join(lobbyId, userService.GetSingle(x => x.DisplayName == "WadeHuang"));

            service.Leave(lobbyId, userService.GetSingle(x => x.DisplayName == "WadeHuang"));

            Assert.AreEqual(2, service.GetById(lobbyId).Users.Count);
            Assert.AreEqual(1, service.GetById(lobbyId).Users.Count(x => x.IsLeave == false));
        }

        [TestMethod()]
        public void LobbyService_Join_Test()
        {
            var chatService = new Mock<IChatService>();

            var service = new LobbyService(chatService.Object);
            var userService = new UserService();
            var lobbyId = service.All().First().Id;
            service.Join(lobbyId, userService.GetSingle(x => x.DisplayName == "test"));
            Assert.AreEqual(1, service.GetById(lobbyId).Users.Count);

            service.Join(lobbyId, userService.GetSingle(x => x.DisplayName == "WadeHuang"));

            Assert.AreEqual(2, service.GetById(lobbyId).Users.Count);
        }
    }
}