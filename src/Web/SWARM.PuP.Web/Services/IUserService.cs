using System;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    public interface IUserService : IBaseService<PuPUser>
    {
        PuPUser Find(string email, string password);
        PuPUser FindByEmail(string email);
    }
}