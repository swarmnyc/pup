using System;
using System.Diagnostics;
using System.Linq;
using System.Linq.Expressions;
using MongoDB;
using MongoDB.Driver.Builders;
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
            var hash = DataProtector.Hash(password);
            return GetSingle(x => x.Email.ToLower() == email.ToLower() && x.PasswordHash == hash);
        }

        public PuPUser FindByEmail(string email)
        {
            return GetSingle(x => x.Email.ToLower() == email.ToLower());
        }

        public PuPUser FindByNameOrEmail(string email, string username)
        {
            return GetSingle(x => x.Email.ToLower() == email.ToLower() || x.UserName.ToLower() == username.ToLower());
        }

        public bool CheckExist(string email, string username)
        {
            return
                Collection.AsQueryable()
                    .Any(x => x.Email.ToLower() == email.ToLower() || x.UserName.ToLower() == username.ToLower());
        }

        public void UpdatePortrait(PuPUser user, string url)
        {
            user.PortraitUrl = url;
            MongoHelper.GetCollection("Lobbies")
                .Update(Query.EQ("Users._id", user.Id), MongoDB.Driver.Builders.Update.Set("Users.$.PortraitUrl", url));
            Update(user);
        }

        protected override Expression<Func<PuPUser, object>> GetOrderExpression(BaseFilter filter)
        {
            return (PuPUser x) => x.Email;
        }
    }
}