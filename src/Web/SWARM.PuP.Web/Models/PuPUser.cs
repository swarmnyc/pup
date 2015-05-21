using System.Collections.Generic;

namespace SWARM.PuP.Web.Models
{
    public class PuPUser : PuPTaggable
    {
        public string UserName { get; set; }
        public virtual string SecurityStamp { get; set; }
        public virtual string Email { get; set; }
        public List<string> Roles { get; set; }
        public virtual string PasswordHash { get; set; }
        public string PictureUrl { get; set; }

        public string GetUserName(GamePlatform platform)
        {
            return this.GetTagValue("Name" + platform) ?? "No Name";
        }

        public void SetUserName(GamePlatform platform, string name)
        {
            this.UpdateTag("Name" + platform, name);
        }
    }
}