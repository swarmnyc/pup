using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Web;
using Microsoft.Azure.NotificationHubs;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web
{
    public static class NotificationHelper
    {
        public static Regex regex = new Regex("^Notification_(?<platform>.+?)_(?<token>.+?)$", RegexOptions.CultureInvariant | RegexOptions.Compiled);

        public static void AddDeviceAsync(PuPUser user, UserDevice device)
        {
            string tag = $"Notification_{device.Platform}_{device.Token}";
            string rid = user.GetTagValue(tag);

            if (string.IsNullOrEmpty(rid))
            {
                QuickbloxPushTokenRoot obj = new QuickbloxPushTokenRoot
                {
                    push_token = new QuickbloxPushToken()
                    {
                        environment = "production",
                        client_identification_sequence = device.Token
                    },
                    device = new QuickbloxDevice()
                    {
                        udid = Guid.NewGuid().ToString("N")
                    }
                };

                string source;
                switch (device.Platform)
                {
                    case DevicePlatform.iOS:
                        source = "apns";
                        obj.device.platform = "iOS";
                        break;
                    case DevicePlatform.Android:
                        source = "gcm";
                        obj.device.platform = "Android";
                        break;
                    default:
                        throw new ArgumentOutOfRangeException(nameof(device.Platform), device.Platform, null);
                }


                var session = QuickbloxHttpHelper.InitSession(user.Id);

                QuickbloxHttpHelper.Create(QuickbloxApiTypes.DeviceRegister, HttpMethod.Post, session).ReadAll(obj);
                QuickbloxHttpHelper.Create(QuickbloxApiTypes.DeviceRegister2, HttpMethod.Post, session).ReadAll(new { notification_channels = source });
            }
        }

        public static async Task DeleteDeviceAsync(PuPUser user, UserDevice device)
        {
            string tag = $"Notification_{device.Platform}_{device.Token}";
            string rid = user.GetTagValue(tag);
            if (string.IsNullOrWhiteSpace(rid))
            {
                NotificationHubClient client = GetClient();

                await client.DeleteRegistrationAsync(rid);
            }
        }

        public static void SendLobbyStart(Lobby lobby, PuPUser user)
        {
            if (lobby.StartTimeUtc < DateTime.UtcNow)
            {
                return;
            }

            foreach (var tag in user.Tags)
            {
                Match match = regex.Match(tag.Key);
                if (match.Success)
                {
                    Notification notification = null;
                    DevicePlatform platform = (DevicePlatform)Enum.Parse(typeof(DevicePlatform), match.Groups["platform"].Value);
                    switch (platform)
                    {
                        case DevicePlatform.iOS:
                            break;
                        case DevicePlatform.Android:
                            notification = new GcmNotification($"{{\"data\":{{\"type\":\"LobbyStart\",\"lobbyId\":\"{lobby.Id}\",\"message\":\"{user.UserName}'s {lobby.Name} will start in 15 mins.\"}}}}");
                            break;
                        case DevicePlatform.Windows:
                            break;
                        default:
                            throw new ArgumentOutOfRangeException();
                    }

                    var task = SendAsnyc(user.Id, notification, lobby.StartTimeUtc.AddMinutes(-15));
                    task.Wait(1);
                }
            }
        }

        public static async Task SendAsnyc(string tag, Notification notification, DateTime? date)
        {
            NotificationHubClient client = GetClient();
            if (date.HasValue)
            {
                await client.ScheduleNotificationAsync(notification, new DateTimeOffset(date.Value.Ticks, TimeSpan.Zero), new[] { tag });
            }
            else
            {
                await client.SendNotificationAsync(notification, new[] { tag });
            }
        }

        private static NotificationHubClient GetClient()
        {
            return NotificationHubClient.CreateClientFromConnectionString(ConfigurationManager.AppSettings["AzureNotificationHub_Key"], ConfigurationManager.AppSettings["AzureNotificationHub_Path"]);
        }
    }
}