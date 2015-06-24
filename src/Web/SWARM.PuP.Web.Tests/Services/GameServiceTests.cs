using System;
using System.Collections.Generic;
using System.Dynamic;
using System.IO;
using Autofac;
using CsvHelper;
using CsvHelper.Configuration;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
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
                Platforms = new List<GamePlatform>() { GamePlatform.PC, GamePlatform.PS4 },
                Tags = new List<PuPTag>() { new PuPTag("GameType", "Shooting"), new PuPTag("GameType", "Action") }
            });

            Assert.IsNotNull(game.Id);
        }
    }
}