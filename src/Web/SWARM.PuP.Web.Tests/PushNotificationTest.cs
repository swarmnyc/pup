using System;
using System.Net;
using System.Net.Http;
using Microsoft.Azure.NotificationHubs;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web.Tests
{
    [TestClass]
    public class PushNotificationTest
    {
        [TestMethod]
        public void Real_MS_RegisterTemplate()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            client.CreateGcmTemplateRegistrationAsync("6514747934631039027-4010090190471628766-8", "{\"data\":{\"type\":\"$(type)\",\"message\":\"$(message)\"}}").Wait();
        }


        [TestMethod]
        public void Read_MS_SendMesssage()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            client.SendGcmNativeNotificationAsync("{\"data\":{\"type\":\"LobbyStart\",\"lobbyId\":\"5594a5b1535c121d6100ff80\",\"message\":\"Wade's Fable II will start in 15 mins.\"}}", "55e9ca17b7569016f0865a2d").Wait();
        }

        [TestMethod]
        public void Read_MS_SendScheduleMesssage()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            Notification notification = new GcmNotification("{\"data\":{\"type\":\"LobbyStart\",\"lobbyId\":\"5594a5b1535c121d6100ff80\",\"message\":\"Wade's Fable II will start in 15 mins.\"}}");
            client.ScheduleNotificationAsync(notification, DateTime.UtcNow, "55e9ca17b7569016f0865a2d").Wait();
        }

        [TestMethod]
        public void Real_QB_RegisterDevice()
        {   
            NotificationHelper.AddDeviceAsync(new PuPUser() {
                Id = "55e9ca17b7569016f0865a2d"
            }, new UserDevice() {
                Platform = DevicePlatform.Android,
                Token = "APA91bEuN31gJdJJhqEWmZi9sRctwknEC0Y7YGDSVqqT9G7R8pgEWiT5vcBRvVeC-N9nitevMQbsoYRvVj2aU0qv0E-j2W9v9W2YHZn2Tbpu99gGB4OWQixFCwBW0RlxYnTFXMHWpjZZ"
            });
        }


        [TestMethod]
        public void Read_QB_SendMesssage()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            client.SendGcmNativeNotificationAsync("{\"data\":{\"type\":\"LobbyStart\",\"lobbyId\":\"5594a5b1535c121d6100ff80\",\"message\":\"Wade's Fable II will start in 15 mins.\"}}", "55e9ca17b7569016f0865a2d").Wait();
        }

        [TestMethod]
        public void Read_QB_SendScheduleMesssage()
        {
            NotificationHubClient client = NotificationHubClient.CreateClientFromConnectionString("Endpoint=sb://ns-pup.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Fwcbge8ii/iQSt82luPFw1mrK+8FWT/3DLWIFfcI6ak=", "pup-notification");

            Notification notification = new GcmNotification("{\"data\":{\"type\":\"LobbyStart\",\"lobbyId\":\"5594a5b1535c121d6100ff80\",\"message\":\"Wade's Fable II will start in 15 mins.\"}}");
            client.ScheduleNotificationAsync(notification, DateTime.UtcNow, "55e9ca17b7569016f0865a2d").Wait();
        }
    }
}
