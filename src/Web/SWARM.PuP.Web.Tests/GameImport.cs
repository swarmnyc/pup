using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Net;
using Autofac;
using CsvHelper;
using CsvHelper.Configuration;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver.Builders;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass]
    public class GameImport
    {
        private readonly IGameService _gameService;

        public GameImport()
        {
            var ioc = TestHelper.GetContainer();
            _gameService = ioc.Resolve<IGameService>();
        }

        [TestMethod]
        public void Real_GameImport_Images()
        {
            var fileFolder = @"D:\Projects\SWARM\PuP\src\Web\SWARM.PuP.Web\Content\Game\";
            var gameCollection = MongoHelper.GetCollection<Game>("Games");
            var client = new WebClient();
            string file1 = null;
            foreach (var game in gameCollection.AsQueryable())
            {
                if (game.PictureUrl.StartsWith("~"))
                {
                    continue;
                }

                file1 = fileFolder + "temp" + Path.GetExtension(game.PictureUrl);
                var file2 = fileFolder + game.Id + ".jpg";
                var file3 = fileFolder + game.Id + "-tn.jpg";
                try
                {
                    client.DownloadFile(game.PictureUrl, file1);
                    CreateThumbnailTo(file1, 2048, file2);
                    CreateThumbnailTo(file1, 128, file3);
                    game.PictureUrl = "~/Content/Game/" + game.Id + ".jpg";
                    game.ThumbnailPictureUrl = "~/Content/Game/" + game.Id + "-tn.jpg";
                }
                catch (Exception ex)
                {
                    Trace.TraceError(game.Name + ":" + ex);
                    game.PictureUrl = "";
                    game.ThumbnailPictureUrl = "";
                }

                gameCollection.Save(game);
            }

            if (!string.IsNullOrWhiteSpace(file1))
            {
                File.Delete(file1);
            }

            client.Dispose();
        }

        [TestMethod]
        public void Real_Clone_And_UpdateId()
        {
            var lobbyCollection = MongoHelper.GetCollection<Lobby>("Lobbies");

            foreach (var lobby in lobbyCollection.FindAll())
            {
                if (lobby.Id != lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId))
                {
                    Debug.WriteLine(lobbyCollection.Remove(Query.EQ("_id", new BsonObjectId(new ObjectId(lobby.Id)))).DocumentsAffected);

                    lobby.Id = lobby.GetTagValue(QuickbloxHttpHelper.Const_ChatRoomId);
                    lobbyCollection.Insert(lobby);
                }
            }
        }

        [TestMethod]
        public void Real_GameImport_SubReddit()
        {
            var gameCollection = MongoHelper.GetCollection<Game>("Games");

            var reader = new CsvReader(new StreamReader(@"D:\Downloads\RedditGameDataForSubReddit.csv"));
            while (reader.Read())
            {
                var gameId = reader.GetField<string>(0);
                var sr = reader.GetField<string>(2);
                if (string.IsNullOrWhiteSpace(sr))
                {
                    gameCollection.Remove(Query.EQ("_id", new ObjectId(gameId)));
                    continue;
                }

                if (sr.EndsWith("/"))
                {
                    sr = sr.Substring(0, sr.Length - 1);
                }

                sr = sr.Substring(sr.LastIndexOf("/") + 1);

                Trace.TraceInformation("{0}:{1}", gameId, sr);
                var game = gameCollection.FindOneById(new ObjectId(gameId));
                game.UpdateTag("RedditSR", sr);
                gameCollection.Save(game);
            }
        }

        [TestMethod]
        public void Real_GameImport_Adds()
        {
            var list = ConvertGameCSV();
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

                _gameService.Add(new Game
                {
                    Name = game.Name,
                    Platforms = p,
                    Genres = game.Genre?.Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries),
                    PictureUrl = game.ImageURL,
                    ThumbnailPictureUrl = game.ImageURL,
                    Description = game.Description,
                    ReleaseDateUtc = date
                });
            }
        }

        private IEnumerable<GameSource> ConvertGameCSV()
        {
            var configuration = new CsvConfiguration();

            configuration.HasHeaderRecord = true;
            configuration.WillThrowOnMissingField = false;
            var reader = new CsvReader(new StreamReader(@"..\..\MockData\import_games.csv"), configuration);

            return reader.GetRecords<GameSource>();
        }

        public void CreateThumbnailTo(string input, int size, string saveto)
        {
            using (var img = Image.FromFile(input))
            {
                var widthRatio = (float)img.Width / size;
                var heightRatio = (float)img.Height / size;
                if (widthRatio > 1 && heightRatio > 1)
                {
                    // Resize to the greatest ratio
                    var ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
                    var newWidth = Convert.ToInt32(Math.Floor(img.Width / ratio));
                    var newHeight = Convert.ToInt32(Math.Floor(img.Height / ratio));

                    var thumb = img.GetThumbnailImage(newWidth, newHeight, ThumbnailImageAbortCallback, IntPtr.Zero);
                    thumb.Save(saveto, ImageFormat.Jpeg);
                    thumb.Dispose();
                }
                else
                {
                    img.Save(saveto, ImageFormat.Jpeg);
                }
            }
        }

        public static bool ThumbnailImageAbortCallback()
        {
            return true;
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