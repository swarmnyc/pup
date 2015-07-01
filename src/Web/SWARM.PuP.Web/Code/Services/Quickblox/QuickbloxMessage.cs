using System.Collections.Generic;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public class QuickbloxMessage
    {
        public QuickbloxMessage()
        {
            send_to_chat = 1;
        }
        
        public string code { get; set; }
        public string codeBody { get; set; }
        public string chat_dialog_id { get; set; }
        public int date_sent { get; set; }
        public int send_to_chat { get; set; }
        public string message { get; set; }
        public int recipient_id { get; set; }
        public int sender_id { get; set; }
        public QuickbloxAttachment[] attachments { get; set; }
    }

    public class QuickbloxMessageQueryResult
    {
        public int limit { get; set; }

        public int skip { get; set; }

        public IList<QuickbloxMessage> items { get; set; }
    }
}