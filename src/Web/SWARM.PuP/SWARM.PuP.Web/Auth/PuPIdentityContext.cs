using System;
using AspNet.Identity.MongoDB;
using MongoDB;
using MongoDB.Driver;

namespace SWARM.PuP.Web.Models
{
    public class PuPIdentityContext : IdentityContext, IDisposable
    {
        public PuPIdentityContext(MongoCollection users, MongoCollection roles) : base(users, roles)
        {
        }

        public void Dispose()
        {
        }

        public static PuPIdentityContext Create()
        {
            var users = MongoHelper.GetCollection<IdentityUser>("Users");
            var roles = MongoHelper.GetCollection<IdentityRole>("Roles");
            return new PuPIdentityContext(users, roles);
        }
    }
}