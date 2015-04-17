using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using MongoDB.Bson;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Tests.ApiControllers
{
    [TestClass()]
    public class GameControllerTests
    {
        public GameControllerTests()
        {
            TestHelper.MockDatabase();
        }

        [TestMethod()]
        public void GameController_Filter_Test()
        {
            var gameService = new GameService();
            var controller = new GameController(gameService);
            var result = controller.Get(new GameFilter()
            {
                Search = "Di",
                Platforms = new List<GamePlatform>() { GamePlatform.PC, GamePlatform.Xbox}
            });

            Console.WriteLine(result.ToMongoQueryText());
            Console.WriteLine(result.ToJson());
            Assert.IsTrue(result.Any());
        }
    }
}