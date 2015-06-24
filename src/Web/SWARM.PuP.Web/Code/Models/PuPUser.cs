using System.Collections.Generic;

namespace SWARM.PuP.Web.Models
{
    public class PuPUser : PuPTaggable
    {
        private HashSet<SocialMedium> _media;
        private HashSet<string> _roles;
        public string UserName { get; set; }
        public virtual string SecurityStamp { get; set; }
        public virtual string Email { get; set; }

        public virtual HashSet<SocialMedium> Media
        {
            get { return _media ?? (_media = new HashSet<SocialMedium>()); }
            set { _media = value; }
        }

        public HashSet<string> Roles
        {
            get { return _roles ?? (_roles = new HashSet<string>()); }
            set { _roles = value; }
        }

        public virtual string PasswordHash { get; set; }
        public string PortraitUrl { get; set; }

        public string GetUserName(GamePlatform platform)
        {
            return this.GetTagValue("Name" + platform) ?? UserName;
        }

        public void SetUserName(GamePlatform platform, string name)
        {
            this.UpdateTag("Name" + platform, name);
        }

        public override string ToString()
        {
            return UserName + "(" + Id + ")";
        }
    }
}