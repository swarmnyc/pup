using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Linq.Expressions;
using MongoDB;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Builders;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    /// <summary>
    ///     wrapper repository class that serves your objects to the MongoDB
    /// </summary>
    public abstract class BaseService<T> : IBaseService<T>
        where T : IMongoModel
    {
        protected BaseService()
        {
            Collection = MongoHelper.GetCollection<T>(typeof(T).Name);
        }

        protected BaseService(string collectionName)
        {
            Collection = MongoHelper.GetCollection<T>(collectionName);
        }

        public MongoCollection<T> Collection { get; private set; }

        public virtual T GetById(string id)
        {
            return Collection.FindOneById(ObjectId.Parse(id));
        }

        public virtual T GetSingle(Expression<Func<T, bool>> criteria)
        {
            return Collection.AsQueryable().SingleOrDefault(criteria);
        }

        public virtual IQueryable<T> Get(Expression<Func<T, bool>> criteria)
        {
            return Collection.AsQueryable().Where(criteria);
        }

        public virtual IQueryable<T> All()
        {
            return Collection.AsQueryable();
        }

        public virtual T Add(T entity)
        {
            entity.UpdatedAtUtc = DateTime.UtcNow;
            Collection.Insert<T>(entity);
            return entity;
        }

        public virtual IEnumerable<T> Add(IEnumerable<T> entities)
        {
            foreach (T entity in entities)
            {
                entity.UpdatedAtUtc = DateTime.UtcNow;
            }

            Collection.InsertBatch<T>(entities);
            return entities;
        }

        public virtual T Update(T entity)
        {
            entity.UpdatedAtUtc = DateTime.UtcNow;
            Collection.Save<T>(entity);
            return entity;
        }

        public virtual IEnumerable<T> Update(IEnumerable<T> entities)
        {
            foreach (var entity in entities) {
                entity.UpdatedAtUtc = DateTime.UtcNow;

                Collection.Save<T>(entity);
            }

            return entities;
        }

        public virtual void Delete(string id)
        {
            Collection.Remove(Query.EQ("_id", new ObjectId(id)));
        }

        public virtual void Delete(T entity)
        {
            Delete(entity.Id);
        }

        public virtual void Delete(Expression<Func<T, bool>> criteria)
        {
            foreach (var entity in Collection.AsQueryable().Where(criteria))
                Delete(entity.Id);
        }

        public virtual void DeleteAll()
        {
            Collection.RemoveAll();
        }

        public virtual long Count()
        {
            return Collection.Count();
        }

        public virtual bool Exists(Expression<Func<T, bool>> criteria)
        {
            return Collection.AsQueryable().Any(criteria);
        }

        protected abstract Expression<Func<T, object>> GetOrderExpression(BaseFilter filter);

        protected virtual IQueryable<T> DoOrderQuery(IQueryable<T> query, BaseFilter filter)
        {
            if (filter.PageIndex != 0)
            {
                query = query.Skip(filter.PageIndex * filter.PageSize);
            }

            if (filter.OrderDirection == ListSortDirection.Ascending)
            {
                query = query.OrderBy(GetOrderExpression(filter));
            }
            else
            {
                query = query.OrderByDescending(GetOrderExpression(filter));
            }

            return query.Take(filter.PageSize);
        }
    }
}