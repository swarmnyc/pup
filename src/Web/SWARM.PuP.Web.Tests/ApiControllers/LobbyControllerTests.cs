﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Security.Claims;
using System.Security.Principal;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;
using IContainer = Autofac.IContainer;

namespace SWARM.PuP.Web.Tests.ApiControllers
{
    [TestClass]
    public class LobbyControllerTests
    {
        private readonly IContainer container;

        public LobbyControllerTests()
        {
            TestHelper.MockDatabase();
            container = TestHelper.GetContainer(b =>
            {
                var chatService = new Mock<IChatService>();
                chatService.Setup(x =>
                    x.CreateRoomForLobby(It.IsAny<PuPUser>(), It.IsAny<Lobby>()))
                    .Callback(
                        new Action<PuPUser, Lobby>((x, y) => { y.AddTag(QuickbloxHttpHelper.Const_ChatRoomId, "Test"); })
                    );

                b.RegisterInstance(chatService.Object).AsImplementedInterfaces();
            });
        }

        [TestMethod]
        public void LobbyController_Filter_Test()
        {
            var controller = container.Resolve<LobbyController>();
            var result = controller.Get(new LobbyFilter
            {
                Order = "Name",
                PageSize = 1,
                UserId = "5567728355187621542717b4",
                OrderDirection = ListSortDirection.Descending,
                PlayStyles = new[] {PlayStyle.Casual}
            });

            Debug.WriteLine(result.ToJson());

            Assert.IsTrue(result.Result.Any());
        }

        [TestMethod]
        public void LobbyController_CreateLobby_Test()
        {
            var userService = new UserService(null);
            var controller = container.Resolve<LobbyController>();

            controller.RequestContext.Principal =
                new GenericPrincipal(
                    new ClaimsIdentity(new List<Claim>
                    {
                        new Claim(ClaimTypes.NameIdentifier, userService.Collection.FindOne().Id)
                    }), null);

            var lobby = controller.Post(new Lobby
            {
                GameId = "test",
                Name = "Test 2",
                PlayStyle = PlayStyle.Casual,
                Platform = GamePlatform.Xbox,
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Hard,
                Description = "Test"
            });

            Assert.AreEqual(1, lobby.Users.Count);
        }
    }
}