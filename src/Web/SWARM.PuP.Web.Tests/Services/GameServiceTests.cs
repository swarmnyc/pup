using System.Collections.Generic;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass()]
    public class GameServiceTests
    {
        private readonly IGameService _gameService;

        public GameServiceTests()
        {
            TestHelper.MockDatabase();
            IContainer ioc = TestHelper.GetContainer();
            _gameService = ioc.Resolve<IGameService>();
        }



        [TestMethod()]
        public void GameService_Add_Test()
        {
            Game game = _gameService.Add(new Game()
            {
                Name = "Test",
                Platforms = new List<GamePlatform>() { GamePlatform.Windows, GamePlatform.PS3 },
                Tags = new List<string>() { "Shooting", "Action" }
            });

            Assert.IsNotNull(game.Id);
        }
    }
}