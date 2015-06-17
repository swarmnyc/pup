using System.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public interface IGameService : IBaseService<Game>
    {
        IQueryable<Game> Filter(GameFilter filter);
    }
}