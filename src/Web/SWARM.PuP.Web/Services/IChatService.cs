using System.Collections.Generic;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    public interface IChatService
    {
        void CreateUser(PuPUser user);
        void CreateRoomForLobby(Lobby lobby);
        void DeleteUser(PuPUser user);
        void SendMessage(Lobby lobby, string message);
        void JoinRoom(Lobby lobby, IEnumerable<string> users);
        void LeaveRoom(Lobby lobby, IEnumerable<string> users);
    }
}