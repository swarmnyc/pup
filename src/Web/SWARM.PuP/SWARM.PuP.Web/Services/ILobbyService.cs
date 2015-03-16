using System.Linq;
using MongoDB;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public interface ILobbyService : IMongoService<Lobby>
    {
        IQueryable<Lobby> Filter(LobbyFilter filter);

    }
}