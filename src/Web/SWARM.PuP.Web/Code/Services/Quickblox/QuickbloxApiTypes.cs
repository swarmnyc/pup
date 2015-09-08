namespace SWARM.PuP.Web.Services.Quickblox
{
    internal static class QuickbloxApiTypes
    {
        internal static string Session = "session.json";
        internal static string User = "users.json";
        internal static string UserDeleteTemplate = "users/{0}.json";
        internal static string Room = "chat/Dialog.json";
        internal static string RoomUpdateTemplate = "chat/Dialog/{0}.json";
        internal static string Message = "chat/Message.json";
        internal static string DeleteMessageTemplate = "chat/Message/{0}.json?force=1";

        internal static string UserDelete(string userId)
        {
            return string.Format(UserDeleteTemplate, userId);
        }

        internal static string RoomUpdate(string roomId)
        {
            return string.Format(RoomUpdateTemplate, roomId);
        }

        internal static string MessageDelete(string messageId)
        {
            return string.Format(DeleteMessageTemplate, messageId);
        }
    }
}