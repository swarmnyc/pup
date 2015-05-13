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

        [TestMethod()]
        public void Real_GameService_Adds_Test()
        {
            IEnumerable<GameSource> list = ConvertGameCSV();
            //IEnumerable<GameSource> list = JsonConvert.DeserializeObject<IEnumerable<GameSource>>(File.ReadAllText(@"..\..\MockData\import_games.json"));

            foreach (var game in list)
            {
                var p = new List<GamePlatform>();
                if (game.XBOX360 == "Y")
                    p.Add(GamePlatform.Xbox360);

                if (game.XBOXOne == "Y")
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
                if (!string.IsNullOrWhiteSpace(game.ReleaseDate))
                    date = DateTime.Parse(game.ReleaseDate).AddHours(-4);

                _gameService.Add(new Game()
                {
                    Name = game.Name,
                    Platforms = p,
                    GameTypes = game.Genre?.Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries),
                    PictureUrl = game.ImageURL,
                    ThumbnailPictureUrl = game.ImageURL,
                    Description = game.Description,
                    ReleaseDateUtc = date,
                });
            }
        }

        private IEnumerable<GameSource> ConvertGameCSV()
        {
            CsvConfiguration configuration = new CsvConfiguration();

            configuration.HasHeaderRecord = true;
            configuration.WillThrowOnMissingField = false;            
            var reader = new CsvHelper.CsvReader(new StreamReader(@"..\..\MockData\import_games.csv"), configuration);
            
            return reader.GetRecords<GameSource>();
        }

        public class GameSource
        {
            public string Name { get; set; }
            public string ReleaseDate { get; set; }
            public string XBOX360 { get; set; }
            public string XBOXOne { get; set; }
            public string PS3 { get; set; }
            public string PS4 { get; set; }
            public string STEAM { get; set; }
            public string PC { get; set; }
            public string WIIU { get; set; }
            public string Description { get; set; }
            public string Genre { get; set; }
            public string ImageURL { get; set; }
        }

    }
}