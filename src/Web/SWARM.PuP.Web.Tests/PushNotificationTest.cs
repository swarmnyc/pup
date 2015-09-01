using System;
using Microsoft.ServiceBus.Notifications;
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

            client.SendGcmNativeNotificationAsync("{\"data\":{\"type\":\"LobbyStart\",\"lobbyId\":\"55d4f72fa28f9ac722001a15\",\"message\":\"Wade's Fable II will start in 15 mins.\"}}").Wait();
        }
    }
}
