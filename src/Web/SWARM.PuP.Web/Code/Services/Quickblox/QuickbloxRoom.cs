namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxRoom
    {
        public string _id { get; set; }
        public object last_message { get; set; }
        public object last_message_date_sent { get; set; }
        public object last_message_user_id { get; set; }
        public string name { get; set; }
        public object photo { get; set; }
        public int[] occupants_ids { get; set; }
        public int type { get; set; }
        public string xmpp_room_jid { get; set; }
        public int unread_messages_count { get; set; }
    }
}