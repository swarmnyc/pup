using System.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public interface ILobbyService : IBaseService<Lobby>
    {
        IQueryable<Lobby> Filter(LobbyFilter filter);

        void Join(string userId, string lobbyId);
        void Leave(string userId, string lobbyId);
    }
}