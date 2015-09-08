using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Web;
using Microsoft.Azure.NotificationHubs;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web
{
    public static class NotificationHubHelper
    {
        public static Regex regex = new Regex("^NH-(?<platform>.+?)-(?<token>.+?)$", RegexOptions.CultureInvariant | RegexOptions.Compiled);

        public static async Task AddDeviceAsync(PuPUser user, UserDevice device)
        {
            string tag = $"NH-{device.Platform}-{device.Token}";
            string rid = user.GetTagValue(tag);
            if (string.IsNullOrEmpty(rid))
            {
                NotificationHubClient client = GetClient();

                string[] tags = { user.Id };
                RegistrationDescription result = null;
                switch (device.Platform)
                {
                    case DevicePlatform.iOS:
                        result = await client.CreateAppleNativeRegistrationAsync(device.Token, tags);
                        break;
                    case DevicePlatform.Android:
                        result = await client.CreateGcmNativeRegistrationAsync(device.Token, tags);
                        break;
                    case DevicePlatform.Windows:
                        result = await client.CreateWindowsNativeRegistrationAsync(device.Token, tags);
                        break;
                    default:
                        throw new ArgumentOutOfRangeException(nameof(device.Platform), device.Platform, null);
                }

                user.AddTag(tag, result.RegistrationId);
            }
        }

        public static async Task DeleteDeviceAsync(PuPUser user, UserDevice device)
        {
            string tag = $"NH-{device.Platform}-{device.Token}";
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