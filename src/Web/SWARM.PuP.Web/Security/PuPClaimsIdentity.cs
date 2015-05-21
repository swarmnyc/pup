using System.Security.Claims;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Security
{
    public sealed class PuPClaimsIdentity : ClaimsIdentity
    {
        public PuPUser User { get; private set; }

        public PuPClaimsIdentity(PuPUser user):base(null,"PuP")
        {
            this.User = user;

            AddClaim(new Claim(this.NameClaimType, user.UserName));
            foreach (var role in user.Roles)
            {
                AddClaim(new Claim(this.RoleClaimType, role));
            }
        }
    }
}