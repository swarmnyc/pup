using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using AspNet.Identity.MongoDB;
using Microsoft.AspNet.Identity;

namespace SWARM.PuP.Web.Models
{
    public class PuPUser : IdentityUser, IPuPTaggable
    {
        private List<PuPTag> _tags;

        public string DisplayName { get; set; }

        public string PictureUrl { get; set; }

        public List<PuPTag> Tags
        {
            get { return _tags ?? (_tags = new List<PuPTag>()); }
            set { _tags = value; }
        }
    }
}