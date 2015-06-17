using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;
using MongoDB;

namespace SWARM.PuP.Web.Services
{
    public interface IBaseService<T> where T : IMongoModel
    {
        T GetById(string id);
        T GetSingle(Expression<Func<T, bool>> criteria);
        IQueryable<T> Get(Expression<Func<T, bool>> criteria);
        IQueryable<T> All();
        T Add(T entity);
        IEnumerable<T> Add(IEnumerable<T> lobbies);
        T Update(T entity);
        IEnumerable<T> Update(IEnumerable<T> entities);
        void Delete(string id);
        void Delete(T entity);
        void Delete(Expression<Func<T, bool>> criteria);
        void DeleteAll();
        long Count();
        bool Exists(Expression<Func<T, bool>> criteria);
    }
}