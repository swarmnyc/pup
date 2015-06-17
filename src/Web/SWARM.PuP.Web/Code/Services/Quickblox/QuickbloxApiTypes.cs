namespace SWARM.PuP.Web.Services.Quickblox
{
    internal static class QuickbloxApiTypes
    {
        internal static string Session = "session.json";
        internal static string User = "users.json";
        internal static string UserDeleteTemplete = "users/{0}.json";
        internal static string Room = "chat/Dialog.json";
        internal static string RoomUpdateTemplete = "chat/Dialog/{0}.json";
        internal static string Message = "chat/Message.json";


        internal static string UserDelete(string userId)
        {
            return string.Format(UserDeleteTemplete, userId);
        }

        internal static string RoomUpdate(string roomId)
        {
            return string.Format(RoomUpdateTemplete, roomId);
        }
    }
}