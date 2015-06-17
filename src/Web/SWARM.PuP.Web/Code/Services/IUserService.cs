using System;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services
{
    public interface IUserService : IBaseService<PuPUser>
    {
        PuPUser Find(string email, string password);
        PuPUser FindByEmail(string email);
        PuPUser FindByNameOrEmail(string email, string username);
        bool CheckExist(string email, string username);
        void UpdatePortrait(PuPUser user, string url);
    }
}