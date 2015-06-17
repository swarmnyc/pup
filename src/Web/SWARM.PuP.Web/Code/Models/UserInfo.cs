using System;

namespace SWARM.PuP.Web.Models
{
    public class UserInfo
    {
        public String Id { get; set; }

        public String UserName { get; set; }

        public String PortraitUrl { get; set; }

        public override bool Equals(object obj)
        {
            if (obj is UserInfo)
            {
                return this.Id.Equals(((UserInfo)obj).Id);
            }
            else
            {
                return false;
            }
        }

        public override int GetHashCode()
        {
            return Id.GetHashCode();
        }

        public override string ToString()
        {
            return string.Format("({0}, {1})", Id, UserName);
        }
    }

    public class LobbyUserInfo : UserInfo
    {
        public bool IsOwner { get; set; }

        public bool IsLeave { get; set; }
    }
}