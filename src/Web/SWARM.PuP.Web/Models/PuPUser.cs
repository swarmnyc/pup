using System;
using System.Collections.Generic;
using System.Linq;
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

        public void UpdateTag(UserTagType type, string key, string value)
        {
            UserTag tag = Tags.FirstOrDefault(t => t.Type == type && t.Key == key);
            if (tag == null)
            {
                tag = new UserTag(type, key, value);
            }
            else
            {
                tag.Value = value;
            }
        }

        public string GetTagValue(UserTagType type, string key)
        {
            return Tags.FirstOrDefault(t => t.Type == type && t.Key == key)?.Value;
        }
    }
}