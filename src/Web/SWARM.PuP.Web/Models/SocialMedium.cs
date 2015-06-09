using System;
using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations;

namespace SWARM.PuP.Web.Models
{
    public class SocialMedium
    {
        [Required]
        public string Type { get; set; }
        [Required]
        public string UserId { get; set; }
        [Required]
        public string Token { get; set; }
        public string Secret { get; set; }
        [Required]
        public DateTime ExpireAtUtc { get; set; }

        public override bool Equals(object obj)
        {
            if (obj is SocialMedium)
            {
                return this.Type.Equals(((SocialMedium)obj).Type);
            }
            else
            {
                return false;
            }
        }

        public override int GetHashCode()
        {
            return Type.GetHashCode();
        }

        public override string ToString()
        {
            return Type;
        }
    }
}