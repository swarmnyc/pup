using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    public interface IChatService
    {
        void CreateUser(PuPUser user);
        
        string CreateRoom(ChatRoomType type, string roomName);
    }
}
