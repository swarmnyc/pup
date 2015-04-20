using System;
using System.Collections.Generic;
using System.Dynamic;
using System.IO;
using Autofac;
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

        [TestMethod()]
        public void Real_GameService_Adds_Test()
        {
            IEnumerable<GameSource> list = JsonConvert.DeserializeObject<IEnumerable<GameSource>>(File.ReadAllText(@"..\..\MockData\import_games2.json"));

            foreach (var game in list)
            {
                var p = new List<GamePlatform>();
                if (game.XBOX_360 == "Y")
                    p.Add(GamePlatform.Xbox360);

                if (game.XBOX == "Y")
                    p.Add(GamePlatform.XboxOne);

                if (game.PS3 == "Y")
                    p.Add(GamePlatform.PS3);

                if (game.PS4 == "Y")
                    p.Add(GamePlatform.PS4);

                if (game.STEAM == "Y")
                    p.Add(GamePlatform.Steam);

                if (game.WIIU == "Y")
                    p.Add(GamePlatform.WiiU);
                
                DateTime? date = null;
                if (!string.IsNullOrWhiteSpace(game.dateReleased))
                    date = DateTime.Parse(game.dateReleased).AddHours(-4);

                _gameService.Add(new Game()
                {
                    Name = game.name,
                    Platforms = p,
                    GameTypes = ((string)game.genre).Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries),
                    PictureUrl = game.imageURL,
                    ThumbnailPictureUrl = game.imageURL,
                    Description = game.description,
                    ReleaseDateUtc = date,
                });
            }
        }

        public class GameSource
        {
            public string name { get; set; }
            public string dateReleased { get; set; }
            public string XBOX_360 { get; set; }
            public string XBOX { get; set; }
            public string PS3 { get; set; }
            public string PS4 { get; set; }
            public string STEAM { get; set; }
            public string PC { get; set; }
            public string WIIU { get; set; }
            public string description { get; set; }
            public string developer { get; set; }
            public string genre { get; set; }
            public string modes { get; set; }
            public string imageURL { get; set; }
        }

    }
}