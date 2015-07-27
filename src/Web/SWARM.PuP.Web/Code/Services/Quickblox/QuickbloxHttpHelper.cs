using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.Services.Quickblox
{
    public static class QuickbloxHttpHelper
    {
        private static readonly string ApplicationId;
        private static readonly string AuthKey;
        private static readonly string AuthSecret;
        private static readonly string AdminUserId;
        private static readonly object LockObj = new object();
        private static readonly Random Random = new Random();
        private static readonly Uri BaseUri;
        private static Session _defaultSession;

        public const string UserPassword = "swarmnyc";
        public const string CompanyEmailDomin = "@swarmnyc.com";
        public const string LobbyNameFormat = "Lobby:{0}";
        public const string Const_ChatRoomId = "QBChatRoomId";
        public const string Const_ChatId = "QBChatId";
        public const string Const_ChatUserName = "QBChatUserName";

        static QuickbloxHttpHelper()
        {
            ApplicationId = ConfigurationManager.AppSettings["Quickblox_ApplicationId"];
            AuthKey = ConfigurationManager.AppSettings["Quickblox_AuthKey"];
            AuthSecret = ConfigurationManager.AppSettings["Quickblox_AuthSecret"];
            AdminUserId = ConfigurationManager.AppSettings["Quickblox_AdminUserId"];
            BaseUri = new Uri(ConfigurationManager.AppSettings["Quickblox_ApiUrl"]);
        }

        internal static WebRequest Create(string api, HttpMethod method)
        {
            if (IsNoSession())
            {
                InitSession();
            }

            return Create(api, method, _defaultSession);
        }

        internal static WebRequest Create(string api, HttpMethod method, Session session)
        {
            WebRequest request = WebRequest.CreateHttp(new Uri(BaseUri, api));
            request.Headers.Add("QB-Token", session.token);
            request.Headers.Add("QuickBlox-REST-API-Version", "0.1.0");
            request.ContentType = "application/json";
            request.Method = method.Method;
            return request;
        }

        internal static string GetChatId(this PuPUser user)
        {
            return user.GetTagValue(Const_ChatId);
        }

        internal static void SetChatId(this PuPUser user, string chatId)
        {
            user.UpdateTag(Const_ChatId, chatId);
        }

        internal static void InitSession()
        {
            if (!IsNoSession())
            {
                return;
            }

            _defaultSession = InitSession(AdminUserId);
        }

        internal static Session InitSession(string userId)
        {
            lock (LockObj)
            {
                WebRequest request = WebRequest.CreateHttp(new Uri(BaseUri, QuickbloxApiTypes.Session));
                request.Headers.Add("QuickBlox-REST-API-Version", "0.1.0");
                request.ContentType = "application/json";
                request.Method = "POST";

                var nonce = GenerateNonce();
                var timestamp = GenerateTimeStamp();
                request.Write(new
                {
                    application_id = ApplicationId,
                    auth_key = AuthKey,
                    timestamp,
                    nonce,
                    signature = GenerateAuthMsg(userId, nonce, timestamp),
                    user = new { login = userId,/* email = UserEmail,*/ password = UserPassword }
                });
                var response = request.GetResponse();
                var date = response.Headers["QB-Token-ExpirationDate"];
                var result = response.Read<SessionResult>();
                if (date.IsNotNullOrWhiteSpace())
                {
                    result.session.ExpiredAt = DateTime.Parse(date.Replace(" UTC", "Z")).ToUniversalTime().AddMinutes(-10);
                }
                return result.session;
            }
        }

        private static bool IsNoSession()
        {
            return _defaultSession == null || DateTime.UtcNow >= _defaultSession.ExpiredAt;
        }

        private static string GenerateTimeStamp()
        {
            var ts = DateTime.UtcNow - new DateTime(1970, 1, 1, 0, 0, 0, 0);
            return Convert.ToInt64(ts.TotalSeconds).ToString();
        }

        private static string GenerateNonce()
        {
            return Random.Next(123400, 9999999).ToString();
        }

        private static String GenerateAuthMsg(string userId, string nonce, string timestamp)
        {
            var messsage = new List<string>
            {
                "application_id=" + ApplicationId,
                "auth_key=" + AuthKey,
                "nonce=" + nonce,
                "timestamp=" + timestamp
            };

            if (AdminUserId != null)
            {
                messsage.Add("user[login]=" + userId);
                //messsage.Add("user[email]=" + UserEmail);
                messsage.Add("user[password]=" + UserPassword);
            }

            var mes = string.Join("&", messsage.OrderBy(m => m).ToArray());

            var sb = new StringBuilder();
            using (var hmac = new HMACSHA1(Encoding.ASCII.GetBytes(AuthSecret)))
            {
                var hash = hmac.ComputeHash(Encoding.ASCII.GetBytes(mes));

                foreach (var b in hash)
                {
                    sb.Append(b.ToString("x2"));
                }
            }

            return sb.ToString();
        }

        public static void ClearSession() {
            _defaultSession = null;
        }
    }
}