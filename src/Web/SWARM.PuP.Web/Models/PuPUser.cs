using System;
using System.Collections.Generic;
using AspNet.Identity.MongoDB;
using MongoDB;

namespace SWARM.PuP.Web.Models
{
    public class PuPUser : IdentityUser, IPuPTaggable, IMongoModel
    {
        private List<PuPTag> _tags;

        public string DisplayName { get; set; }

        public string PictureUrl { get; set; }

        public List<PuPTag> Tags
        {
            get { return _tags ?? (_tags = new List<PuPTag>()); }
            set { _tags = value; }
        }

        public string GetUserName(GamePlatform platform)
        {
            return this.GetTagValue("Name" + platform) ?? "No Name";
        }

        public void SetUserName(GamePlatform platform, string name)
        {
            this.UpdateTag("Name" + platform, name);
        }

        public DateTime UpdatedAtUtc { get; set; }
    }
}