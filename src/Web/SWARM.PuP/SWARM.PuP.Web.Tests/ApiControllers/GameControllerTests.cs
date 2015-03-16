using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SWARM.PuP.Web.ApiControllers.Tests
{
    [TestClass()]
    public class GameControllerTests
    {
        [TestMethod()]
        public void GameController_GetsTest()
        {
            var gameService = new GameService(); //TODO : Change to Mock
            var controller = new GameController(gameService);
            var result = controller.Get(new GameFilter()
            {
                Search = "Di",
                Platforms = new List<GamePlatform>() { GamePlatform.Windows, GamePlatform.Xbox360 }
            });

            Console.Write(result.ToMongoQueryText());
            
            Assert.IsNotNull(result);
            Assert.IsTrue(result.Any());
        }
    }
}