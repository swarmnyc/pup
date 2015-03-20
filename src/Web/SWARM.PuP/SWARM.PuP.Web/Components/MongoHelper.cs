using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Linq.Expressions;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using MongoDB.Driver.Linq;

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
    /// wrapper repository class that serves your objects to the MongoDB
    /// </summary>
    /// 
    public abstract class MongoService<T> : IMongoService<T> where T : MongoModel
    {
        protected MongoService()
        {
            this.Collection = MongoHelper.GetCollection<T>(typeof(T).Name);
            }

        protected MongoService(string collectionName)
        {
            this.Collection = MongoHelper.GetCollection<T>(collectionName);
        }

        public MongoCollection<T> Collection { get; private set; }

        public virtual  T GetById(string id)
        {
            return this.Collection.FindOneByIdAs<T>(id);
        }

        public virtual T GetSingle(Expression<Func<T, bool>> criteria)
        {
            return this.Collection.AsQueryable<T>().Where(criteria).Single();
        }

        public virtual IQueryable<T> Get(Expression<Func<T, bool>> criteria)
        {
            return this.Collection.AsQueryable<T>().Where(criteria);
        }

        public virtual IQueryable<T> All()
        {
            return this.Collection.AsQueryable<T>();
        }
   
        public virtual T Add(T entity)
        {
            this.Collection.Insert<T>(entity);
            return entity;
        }

        public virtual IEnumerable<T> Add(IEnumerable<T> lobbies)
        {
            this.Collection.InsertBatch<T>(lobbies);
            return lobbies;
        }

        public virtual T Update(T entity)
        {
            this.Collection.Save<T>(entity);
            return entity;
        }

        public virtual IEnumerable<T> Update(IEnumerable<T> entities)
        {
            foreach (T entity in entities)
                this.Collection.Save<T>(entity);

            return entities;
        }

        public virtual void Delete(string id)
        {
            this.Collection.Remove(Query.EQ("_id", new ObjectId(id)));
        }

        public virtual void Delete(T entity)
        {
            this.Delete(entity.Id.ToString());
        }

        public virtual void Delete(Expression<Func<T, bool>> criteria)
        {
            foreach (T entity in this.Collection.AsQueryable<T>().Where(criteria))
                this.Delete(entity.Id.ToString());
        }

        public virtual void DeleteAll()
        {
            this.Collection.RemoveAll();
        }

        public virtual long Count()
        {
            return this.Collection.Count();
        }

        public virtual bool Exists(Expression<Func<T, bool>> criteria)
        {
            return this.Collection.AsQueryable<T>().Any(criteria);
        }
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