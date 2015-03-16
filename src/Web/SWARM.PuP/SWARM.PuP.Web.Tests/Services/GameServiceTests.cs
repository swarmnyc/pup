using Microsoft.VisualStudio.TestTools.UnitTesting;
using SWARM.PuP.Web.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Autofac;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services.Tests
{
    [TestClass()]
    public class GameServiceTests
    {
        private readonly IGameService _gameService;

        public GameServiceTests()
        {
            ContainerBuilder builder = new ContainerBuilder();
            builder.RegisterAssemblyTypes(typeof(IChatService).Assembly).Where(x => x.FullName.EndsWith("Service")).AsImplementedInterfaces();

            IContainer ioc = builder.Build();
            _gameService = ioc.Resolve<IGameService>();
        }

        [TestMethod()]
        public void AddTest()
        {
            Game game = _gameService.Add(new Game()
            {
                Name = "Test",
                Platforms = new List<GamePlatform>() { GamePlatform.Windows, GamePlatform.PS3 },
                Tags = new List<string>() { "Shooting", "Action"}
            });

            Assert.IsNotNull(game.Id);            
        }
    }
}