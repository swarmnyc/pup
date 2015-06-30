using System.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public interface ILobbyService : IBaseService<Lobby>
    {
        IQueryable<Lobby> Filter(LobbyFilter filter);

        void Join(string lobbyId, PuPUser user);

        void Leave(string lobbyId, PuPUser user);

        Lobby Add(Lobby lobby, PuPUser owner);
    }
}