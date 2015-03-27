using System;
using System.Collections.Generic;
using System.IO;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver.Linq;
using MongoDB.Embedded;
using Newtonsoft.Json;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Tests
{
    [TestClass()]
    public class TestHelper
    {
        public static IContainer GetContainer(Action<ContainerBuilder> extraBinding = null)
        {
            ContainerBuilder builder = new ContainerBuilder();
            builder.RegisterAssemblyTypes(typeof(PuPUser).Assembly).Where(x => x.FullName.EndsWith("Service")).AsImplementedInterfaces();
            if (extraBinding != null)
            {
                extraBinding(builder);
            }

            return builder.Build();
        }

        
        public static void MockDatabase()
        {
            var mongoEmbedded = new EmbeddedMongoDbServer();
            var mongoServer = mongoEmbedded.Client.GetServer();
            var db = mongoServer.GetDatabase("UnitTest");
            MongoHelper.SetDatabase(db);

            //Initialize MockData
            var json = File.ReadAllText("MockData\\games.json");
            MongoHelper.GetCollection<Game>("Games").InsertBatch(JsonConvert.DeserializeObject<List<Game>>(json));

            json = File.ReadAllText("MockData\\lobbies.json");
            MongoHelper.GetCollection<Lobby>("Lobbies").InsertBatch(JsonConvert.DeserializeObject<List<Lobby>>(json));
        }

        [TestMethod]
        public void Real_DumpDatabase()
        {
            File.WriteAllText("..\\..\\MockData\\games.json", JsonConvert.SerializeObject(MongoHelper.GetCollection<Game>("Games").FindAll()));
            File.WriteAllText("..\\..\\MockData\\lobbies.json", JsonConvert.SerializeObject(MongoHelper.GetCollection<Lobby>("Lobbies").FindAll()));

        }
    }
}