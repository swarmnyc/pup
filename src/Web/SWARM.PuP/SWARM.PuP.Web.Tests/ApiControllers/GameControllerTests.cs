using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Tests.ApiControllers
{
    [TestClass()]
    public class GameControllerTests
    {
        [TestMethod()]
        public void GameController_Filter_Test()
        {
            var gameService = new GameService();
            var controller = new GameController(gameService);
            var result = controller.Get(new GameFilter()
            {
                Search = "Di",
                Platforms = new List<GamePlatform>() { GamePlatform.Windows, GamePlatform.Xbox360 }
            });

            Console.Write(result.ToMongoQueryText());
            
            Assert.IsTrue(result.Any());
        }
    }
}