using System;

namespace SWARM.PuP.Web.Models
{
    public class UserInfo
    {
        public String Id { get; set; }

        public String Name { get; set; }

        public String PictureUrl { get; set; }

        public override bool Equals(object obj)
        {
            return this.Id.Equals(obj);
        }

        public override int GetHashCode()
        {
            return Id.GetHashCode();
        }

        public override string ToString()
        {
            return string.Format("({0}, {1})", Id, Name);
        }
    }

    public class LobbyUserInfo : UserInfo
    {
        public bool IsOwner { get; set; }

        public bool IsLeave { get; set; }
    }
}