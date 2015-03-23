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
                Name = "Test 2",
                PlayStyle = PlayStyle.Serious,
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Pro,
                Description = "Test"
            });

            Assert.IsNotNull(lobby);
            Assert.IsNotNull(lobby.ChatRoomId);
        }
    }
}