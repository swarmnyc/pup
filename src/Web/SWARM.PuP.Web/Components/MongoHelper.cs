using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web.Http.ModelBinding;
using Microsoft.Ajax.Utilities;
using MongoDB.Bson;
using MongoDB.Bson.Serialization;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Bson.Serialization.Conventions;
using MongoDB.Bson.Serialization.Options;
using MongoDB.Driver;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;

namespace MongoDB
{
    public interface IMongoModel
    {
        string Id { get; }

        DateTime UpdatedAtUtc { get; set; }

    }

    /// <summary>
    /// class to wrap up your objects for Mongo Happiness
    /// </summary>
    [BsonIgnoreExtraElements(Inherited = true)]
    public abstract class MongoModel : IMongoModel
    {
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }

        public DateTime UpdatedAtUtc { get; set; }

        [BsonRepresentation(BsonType.String)]
        public ModelState State { get; set; }
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
                if (ConfigurationManager.ConnectionStrings["DefaultConnection"] != null)
                {
                    //Create Database from ConnectionString
                    var url = new MongoUrl(ConfigurationManager.ConnectionStrings["DefaultConnection"].ConnectionString);
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

        public static void SetDatabase(MongoDatabase database)
        {
            _database = database;
        }

        public static MongoCollection<T> GetCollection<T>(string collectionName)
        {
            return GetDatabase().GetCollection<T>(collectionName);
        }

        public static IMongoQuery ToMongoQuery<T>(this IQueryable<T> query)
        {
            MongoQueryable<T> mongoQueryable = query as MongoQueryable<T>;
            if (mongoQueryable != null)
                return mongoQueryable.GetMongoQuery();
            MongoCursor<T> mongoCursor = query as MongoCursor<T>;
            if (mongoCursor != null)
                return mongoCursor.Query;
            throw new ArgumentException(string.Format("Cannot convert from {0} to either {1} or {2}.", (object)query.GetType().Name, (object)typeof(MongoQueryable<T>).Name, (object)typeof(MongoCursor<T>).Name));
        }

        public static string ToMongoQueryText<T>(this IQueryable<T> query)
        {
            IMongoQuery mongoQuery = ToMongoQuery<T>(query);
            if (mongoQuery == null)
                return (string)null;
            return mongoQuery.ToString();
        }
    }
}