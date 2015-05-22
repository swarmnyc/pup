using System;

namespace SWARM.PuP.Web.Security
{
    public class AccessToken
    {
        public string Id { get; set; }

        public DateTime ExpirationDateUtc { get; set; }

        public AccessToken()
        {
        }

        public AccessToken(string id, DateTime expirationDateUtc)
        {
            Id = id;
            ExpirationDateUtc = expirationDateUtc;
        }

        public AccessToken(string id)
        {
            Id = id;
            ExpirationDateUtc = DateTime.UtcNow.AddYears(1);
        }
    }
}