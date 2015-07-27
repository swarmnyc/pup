using System.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.Services
{
    public interface ILobbyService : IBaseService<Lobby>
    {
        LobbySearchResult Filter(LobbyFilter filter);

        void Join(string lobbyId, PuPUser user);

        void Leave(string lobbyId, PuPUser user);

        Lobby Add(Lobby lobby, PuPUser owner);
    }
}