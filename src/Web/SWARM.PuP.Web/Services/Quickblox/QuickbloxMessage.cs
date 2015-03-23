namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxMessage
    {
        public string _id { get; set; }
        public string age { get; set; }
        public QuickbloxAttachment[] attachments { get; set; }
        public string chat_dialog_id { get; set; }
        public int date_sent { get; set; }
        public string message { get; set; }
        public int recipient_id { get; set; }
        public int sender_id { get; set; }
    }
}