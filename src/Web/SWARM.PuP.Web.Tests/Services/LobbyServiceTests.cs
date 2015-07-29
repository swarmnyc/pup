using System;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass]
    public class LobbyServiceTests
    {
        public LobbyServiceTests()
        {
            TestHelper.MockDatabase();
        }

        [TestMethod]
        public void LobbyService_AddLobby_Test()
        {
            var chatService = new Mock<IChatService>();
            chatService.Setup(x => x.CreateRoomForLobby(It.IsAny<PuPUser>(), It.IsAny<Lobby>()))
                .Callback(
                    new Action<PuPUser, Lobby>((x, y) => { y.AddTag(QuickbloxHttpHelper.Const_ChatRoomId, "Test"); }));

            var userService = new UserService(null);
            var service = new LobbyService(chatService.Object, null);
            var lobby = service.Add(new Lobby
            {
                GameId = "test",
                Name = "Test 2",
                PlayStyle = PlayStyle.Casual,
                Platform = GamePlatform.Xbox360,
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Hard,
                Description = "Test"
            }, userService.Collection.FindOne());

            Assert.IsNotNull(lobby);
            Assert.IsNotNull(lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId));
        }

        [TestMethod]
        public void LobbyService_Leave_Test()
        {
            var chatService = new Mock<IChatService>();

            var service = new LobbyService(chatService.Object, null);
            var lobbyId = service.Collection.FindOne().Id;
            var userService = new UserService(null);
            service.Join(lobbyId, userService.GetSingle(x => x.UserName == "test"));
            service.Join(lobbyId, userService.GetSingle(x => x.UserName == "WadeHuang"));

            service.Leave(lobbyId, userService.GetSingle(x => x.UserName == "WadeHuang"));

            Assert.AreEqual(2, service.GetById(lobbyId).Users.Count);
            Assert.AreEqual(1, service.GetById(lobbyId).Users.Count(x => x.IsLeave == false));
        }

        [TestMethod]
        public void LobbyService_Join_Test()
        {
            var chatService = new Mock<IChatService>();

            var service = new LobbyService(chatService.Object, null);
            var userService = new UserService(null);
            var lobbyId = service.All().First().Id;
            service.Join(lobbyId, userService.GetSingle(x => x.UserName == "test"));
            Assert.AreEqual(1, service.GetById(lobbyId).Users.Count);

            service.Join(lobbyId, userService.GetSingle(x => x.UserName == "WadeHuang"));
            service.Join(lobbyId, userService.GetSingle(x => x.UserName == "WadeHuang"));
            service.Join(lobbyId, userService.GetSingle(x => x.UserName == "WadeHuang"));

            Assert.AreEqual(2, service.GetById(lobbyId).Users.Count);
        }

        [TestMethod]
        public void LobbyService_Leave_ChangeOwnShip_Test()
        {
            var chatService = new Mock<IChatService>();
            var userService = new UserService(null);
            var service = new LobbyService(chatService.Object, null);
            var lobby = service.Add(new Lobby
            {
                GameId = "test",
                Name = "Test 2",
                PlayStyle = PlayStyle.Normal,
                Platform = GamePlatform.Xbox360,
                StartTimeUtc = DateTime.UtcNow.AddHours(1),
                SkillLevel = SkillLevel.Hard,
                Description = "Test"
            }, userService.GetSingle(x => x.UserName == "test"));

            service.Join(lobby.Id, userService.GetSingle(x => x.UserName == "WadeHuang"));

            service.Leave(lobby.Id, userService.GetSingle(x => x.UserName == "test"));

            Assert.AreEqual(userService.GetSingle(x => x.UserName == "WadeHuang").Id,
                service.GetById(lobby.Id).Users.First(x => x.IsOwner).Id);
        }

        [TestMethod]
        public void Real_LobbyService_AddLobbys_Test()
        {
            var gameService = new GameService();
            var userService = new UserService(null);
            var service = new LobbyService(new QuickbloxChatService(), null);

            var random = new Random();
            foreach (var game in gameService.All().OrderBy(x => x.Id).Take(30))
            {
                var lobby = service.Add(new Lobby
                {
                    GameId = game.Id,
                    Name = game.Name,
                    PlayStyle = PlayStyle.Casual,
                    Platform = game.Platforms.First(),
                    PictureUrl = game.PictureUrl,
                    ThumbnailPictureUrl = game.ThumbnailPictureUrl,
                    StartTimeUtc = DateTime.UtcNow.AddMinutes(random.Next(1, 100)),
                    SkillLevel = SkillLevel.Hard,
                    Description = "Let's play it"
                }, userService.Collection.FindOne());
            }
        }
    }
}