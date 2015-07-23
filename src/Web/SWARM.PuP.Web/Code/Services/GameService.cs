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

            query = query.Where(x => x.State == ModelState.Active);

            query = DoOrderQuery(query, filter);

            return query;
        }

        protected override Expression<Func<Game, object>> GetOrderExpression(BaseFilter filter)
        {
            switch ((filter.Order ?? "").ToLower())
            {
                case "rank":
                    return (Game x) => x.Rank;
                case "name":
                default:
                    return (Game x) => x.Name;
            }
        }
    }
}