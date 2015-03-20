using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    public interface IChatService
    {
        void CreateUser(PuPUser user);
        string CreateRoom(ChatRoomType type, string roomName);
    }
}