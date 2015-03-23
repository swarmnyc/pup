using System;

namespace SWARM.PuP.Web.Services.Quickblox
{
    internal class Session
    {
        public string _id { get; set; }
        public int application_id { get; set; }
        public DateTime created_at { get; set; }
        public int device_id { get; set; }
        public int nonce { get; set; }
        public string token { get; set; }
        public int ts { get; set; }
        public DateTime updated_at { get; set; }
        public int user_id { get; set; }
        public int id { get; set; }
    }
}