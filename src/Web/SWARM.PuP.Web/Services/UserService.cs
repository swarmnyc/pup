using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Web;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;

namespace SWARM.PuP.Web.Services
{
    public class UserService : BaseService<PuPUser>, IUserService
    {
        public UserService() : base("Users")
        {
        }

        protected override Expression<Func<PuPUser, object>> GetOrderExpression(BaseFilter filter)
        {
            return (PuPUser x) => x.Email;
        }
    }
}