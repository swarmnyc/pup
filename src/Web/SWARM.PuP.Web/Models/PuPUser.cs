using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using AspNet.Identity.MongoDB;
using Microsoft.AspNet.Identity;

namespace SWARM.PuP.Web.Models
{
    public class PuPUser : IdentityUser
    {
        private List<UserTag> _tags;

        public string DisplayName { get; set; }

        public string PictureUrl { get; set; }

        public List<UserTag> Tags
        {
            get { return _tags ?? (_tags = new List<UserTag>()); }
            set { _tags = value; }
        }
    }
}