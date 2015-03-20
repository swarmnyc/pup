using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using MongoDB.Driver.Linq;

namespace MongoDB
{
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
}