using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Web;
using MongoDB.Driver;
using MongoDB.Driver.Linq;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Security;

namespace SWARM.PuP.Web.Services
{
    public class UserService : BaseService<PuPUser>, IUserService
    {
        private readonly IChatService _chatService;

        public UserService(IChatService chatService) : base("Users")
        {
            _chatService = chatService;
        }

        public override PuPUser Add(PuPUser user)
        {
            _chatService.CreateUser(user);

            return base.Add(user);
        }

        public PuPUser Find(string email, string password)
        {
            string hash = DataProtector.Hash(password);
            return GetSingle(x => x.Email.ToLower() == email.ToLower() && x.PasswordHash == hash);
        }

        public PuPUser FindByEmail(string email)
        {
            return GetSingle(x => x.Email.ToLower() == email.ToLower());
        }

        public bool CheckExist(string email, string username)
        {
            return this.Collection.AsQueryable().Any(x=>x.Email.ToLower()==email.ToLower() || x.UserName.ToLower()==username.ToLower());
        }

        protected override Expression<Func<PuPUser, object>> GetOrderExpression(BaseFilter filter)
        {
            return (PuPUser x) => x.Email;
        }
    }
}