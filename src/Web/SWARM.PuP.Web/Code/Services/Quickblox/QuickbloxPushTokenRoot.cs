namespace SWARM.PuP.Web.Services.Quickblox {
    public class QuickbloxPushTokenRoot
    {
        public QuickbloxPushToken push_token { get; set; }
        public QuickbloxDevice device { get; set; }
    }

    public class QuickbloxPushToken
    {
        public string environment { get; set; }
        public string client_identification_sequence { get; set; }
    }

    public class QuickbloxDevice
    {
        public string platform { get; set; }
        public string udid { get; set; }
    }

}