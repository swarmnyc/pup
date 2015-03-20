using System;
using System.Linq;
using MongoDB;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public class GameService : MongoService<Game>, IGameService
    {
        public GameService() : base("Games")
        {
        }

        public IQueryable<Game> Filter(GameFilter filter)
        {
            var query = this.All();

            filter = filter ?? new GameFilter();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(x => x.Name.ToLower().Contains(filter.Search));
            }

            if (!filter.Platforms.IsNullOrEmpty())
            {
                query = query.Where(x => x.Platforms.ContainsAny(filter.Platforms));
            }

            query = filter.DoOrderQuery(query);

            return query;
        }
    }
}