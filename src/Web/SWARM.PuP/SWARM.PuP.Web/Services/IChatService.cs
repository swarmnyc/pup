using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    internal interface IChatService
    {
        string CreateUser(PuPUser user);
    }
}
