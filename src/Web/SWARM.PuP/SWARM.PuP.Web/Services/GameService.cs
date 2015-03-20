using System;
using System.Linq;
using System.Linq.Expressions;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public class GameService : BaseService<Game>, IGameService
    {
        public GameService() : base("Games")
        {
        }

        public IQueryable<Game> Filter(GameFilter filter)
        {
            var query = All();

            filter = filter ?? new GameFilter();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(x => x.Name.ToLower().Contains(filter.Search));
            }

            if (!filter.Platforms.IsNullOrEmpty())
            {
                query = query.Where(x => x.Platforms.ContainsAny(filter.Platforms));
            }

            query = DoOrderQuery(query, filter);

            return query;
        }

        protected override Expression<Func<Game, object>> GetOrderExpression(BaseFilter filter)
        {
            if (string.IsNullOrWhiteSpace(filter.Order))
            {
                return (Game x) => x.Name;
            }
            switch (filter.Order.ToLower())
            {
                case "name":
                default:
                    return (Game x) => x.Name;
            }
        }
    }
}