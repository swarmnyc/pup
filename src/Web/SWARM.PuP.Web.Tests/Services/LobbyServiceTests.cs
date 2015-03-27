using System;
using System.Web.UI.WebControls;
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
            chatService.Setup(x => x.CreateRoomForLobby(It.IsAny<Lobby>())).Callback(new Action<Lobby>(x =>
            {
                x.AddTag("ChatRoomId", "Test");
            }));

            var service = new LobbyService(chatService.Object);
            var lobby = service.Add(new Lobby()
            {
                GameId = "test",
                Name = "Test 2",
                PlayStyle = PlayStyle.Serious,
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Pro,
                Description = "Test"
            });

            Assert.IsNotNull(lobby);
            Assert.IsNotNull(lobby.GetTagValue("ChatRoomId"));
        }
    }
}