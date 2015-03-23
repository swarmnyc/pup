using System;
using System.Configuration;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Driver;

namespace MongoDB
{
    /// <summary>
    /// class to wrap up your objects for Mongo Happiness
    /// </summary>
    [BsonIgnoreExtraElements(Inherited = true)]
    public abstract class MongoModel
    {
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }
    }

    /// <summary>
    /// class that spawns MonogDatabase and MongoCollection objects
    /// </summary>
    public static class MongoHelper
    {
        private static MongoDatabase _database;

        public static MongoDatabase GetDatabase()
        {
            if (_database == null)
            {
                if (ConfigurationManager.ConnectionStrings.Count > 1)
                {
                    var url = new MongoUrl(ConfigurationManager.ConnectionStrings[1].ConnectionString);
                    var client = new MongoClient(url);
                    _database = client.GetServer().GetDatabase(url.DatabaseName);
                }
                else
                {
                    throw new InvalidOperationException("Need a connection string in your app/web.config file");
                }
            }

            return _database;
        }

        public static MongoCollection<T> GetCollection<T>(string collectionName)
        {
            return GetDatabase().GetCollection<T>(collectionName);
        }
    }
}