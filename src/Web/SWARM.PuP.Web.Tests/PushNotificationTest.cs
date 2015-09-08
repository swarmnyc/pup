using System;
using Microsoft.Azure.NotificationHubs;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace SWARM.PuP.Web.Tests
{
    [TestClass]
    public class PushNotificationTest
    {
        [TestMethod]
        public void Real_RegisterTemplate()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            client.CreateGcmTemplateRegistrationAsync("6514747934631039027-4010090190471628766-8", "{\"data\":{\"type\":\"$(type)\",\"message\":\"$(message)\"}}").Wait();
        }


        [TestMethod]
        public void Read_SendMesssage()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            client.SendGcmNativeNotificationAsync("{\"data\":{\"type\":\"LobbyStart\",\"lobbyId\":\"5594a5b1535c121d6100ff80\",\"message\":\"Wade's Fable II will start in 15 mins.\"}}", "55e9ca17b7569016f0865a2d").Wait();
        }

        [TestMethod]
        public void Read_SendScheduleMesssage()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            Notification notification = new GcmNotification("{\"data\":{\"type\":\"LobbyStart\",\"lobbyId\":\"5594a5b1535c121d6100ff80\",\"message\":\"Wade's Fable II will start in 15 mins.\"}}");
            client.ScheduleNotificationAsync(notification, DateTime.UtcNow, "55e9ca17b7569016f0865a2d").Wait();
        }
    }
}
