using System.Collections.Generic;

namespace SWARM.PuP.Web.Models
{
    public class PuPUser : PuPTaggable
    {
        private IList<string> _roles;

        public string UserName { get; set; }
        public virtual string SecurityStamp { get; set; }
        public virtual string Email { get; set; }

        public IList<string> Roles
        {
            get { return _roles ?? (_roles = new List<string>()); }
            set { _roles = value; }
        }

        public virtual string PasswordHash { get; set; }
        public string PictureUrl { get; set; }

        public string GetUserName(GamePlatform platform)
        {
            return this.GetTagValue("Name" + platform) ?? UserName;
        }

        public void SetUserName(GamePlatform platform, string name)
        {
            this.UpdateTag("Name" + platform, name);
        }
    }
}