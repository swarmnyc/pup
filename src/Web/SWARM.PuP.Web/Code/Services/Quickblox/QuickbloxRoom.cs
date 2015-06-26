using System.Collections.Generic;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxRoom
    {
        public string _id { get; set; }
        public string last_message { get; set; }
        public int last_message_date_sent { get; set; }
        public string last_message_user_id { get; set; }
        public string name { get; set; }
        public string photo { get; set; }
        public int[] occupants_ids { get; set; }
        public int type { get; set; }
        public string xmpp_room_jid { get; set; }
        public int unread_messages_count { get; set; }
    }

    public class QuickbloxRoomQueryResult {
        public int limit { get; set; }

        public int skip { get; set; }

        public IList<QuickbloxRoom> items {get;set;}
    }
}