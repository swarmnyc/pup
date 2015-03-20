using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass()]
    public class LobbyServiceTests
    {
        [TestMethod()]
        public void LobbyService_AddLobby_Test()
        {
            var service = new LobbyService(new QuickbloxChatService());
            var lobby = service.Add(new Lobby()
            {
                GameId = "test",
                Name = "test",
                PlayStyle = PlayStyle.Casual,
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Newbie,
                Description = "Test"
            });

            Assert.IsNotNull(lobby);
            Assert.IsNotNull(lobby.ChatRoomId);
        }
    }
}