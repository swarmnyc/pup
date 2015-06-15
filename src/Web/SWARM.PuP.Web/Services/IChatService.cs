using System.Collections.Generic;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    public interface IChatService
    {
        void CreateUser(PuPUser user);
        void CreateRoomForLobby(PuPUser owner, Lobby lobby);
        void DeleteUser(PuPUser user);
        void JoinRoom(Lobby lobby, IEnumerable<PuPUser> users);
        void LeaveRoom(Lobby lobby, IEnumerable<PuPUser> users);
    }
}