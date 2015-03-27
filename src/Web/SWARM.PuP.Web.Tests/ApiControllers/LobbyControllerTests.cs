using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Security.Claims;
using System.Security.Principal;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using MongoDB.Bson;
using Moq;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;
using IContainer = Autofac.IContainer;

namespace SWARM.PuP.Web.Tests.ApiControllers
{
    [TestClass()]
    public class LobbyControllerTests
    {
        private readonly IContainer container;

        public LobbyControllerTests()
        {
            TestHelper.MockDatabase();
            container = TestHelper.GetContainer((b) =>
            {
                var chatService = new Mock<IChatService>();
                chatService.Setup(x => x.CreateRoomForLobby(It.IsAny<Lobby>())).Callback(new Action<Lobby>(x =>
                {
                    x.AddTag(QuickbloxHttpHelper.Const_ChatRoomId, "Test");
                }));

                b.RegisterInstance(chatService.Object).AsImplementedInterfaces();
            });
        }

        [TestMethod()]
        public void LobbyController_Filter_Test()
        {
            LobbyController controller = new LobbyController(container.Resolve<ILobbyService>());
            var result = controller.Get(new LobbyFilter()
            {
                Order = "Name",
                PageSize = 1,
                OrderDirection = ListSortDirection.Descending,
                PlayStyles = new[] { PlayStyle.Casual }
            });

            Console.WriteLine(result.ToMongoQueryText());
            Console.WriteLine(result.ToJson());

            Assert.IsTrue(result.Any());
        }

        [TestMethod()]
        public void LobbyController_CreateLobby_Test()
        {
            LobbyController controller = new LobbyController(container.Resolve<ILobbyService>());
            controller.RequestContext.Principal = new GenericPrincipal(new ClaimsIdentity(new List<Claim>() { new Claim(ClaimTypes.NameIdentifier, "SWARM") }), null);
            var lobby = controller.Post(new Lobby()
            {
                GameId = "test",
                Name = "Test 2",
                PlayStyle = PlayStyle.Serious,
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Pro,
                Description = "Test"
            });

            Assert.IsTrue(lobby.UserIds.Count > 0);
        }
    }
}