using System;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxUser
    {
        public int id { get; set; }
        public int owner_id { get; set; }
        public object full_name { get; set; }
        public string email { get; set; }
        public string login { get; set; }
        public object phone { get; set; }
        public object website { get; set; }
        public DateTime created_at { get; set; }
        public DateTime updated_at { get; set; }
        public object last_request_at { get; set; }
        public object external_user_id { get; set; }
        public object facebook_id { get; set; }
        public object twitter_id { get; set; }
        public object blob_id { get; set; }
        public object custom_data { get; set; }
        public object user_tags { get; set; }
    }
}