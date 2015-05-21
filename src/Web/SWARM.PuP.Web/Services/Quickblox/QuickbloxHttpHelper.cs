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
        private const int TimeOut = 19;
        private static readonly string ApplicationId;
        private static readonly string AuthKey;
        private static readonly string AuthSecret;
        private static readonly string AdminUserId;
        private static readonly object LockObj = new object();
        private static readonly Random Random = new Random();
        private static readonly Uri BaseUri;
        private static Session _session;

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

            WebRequest request = WebRequest.CreateHttp(new Uri(BaseUri, api));
            request.Headers.Add("QB-Token", _session.token);
            request.Headers.Add("QuickBlox-REST-API-Version", "0.1.0");
            request.ContentType = "application/json";
            request.Method = method.Method;
            return request;
        }

        internal static string GetChatId(this PuPUser user)
        {
            return user.GetTagValue(Const_ChatId);
        }

        internal static void SetChatId(this PuPUser user, string chatId,string username)
        {
            user.UpdateTag(Const_ChatId, chatId);
            user.UpdateTag(Const_ChatUserName, username);
        }

        private static bool IsNoSession()
        {
            return _session == null || (_session.created_at - DateTime.UtcNow).Minutes > TimeOut;
        }

        private static void InitSession()
        {
            lock (LockObj)
            {
                if (!IsNoSession())
                {
                    return;
                }

                WebRequest request = WebRequest.CreateHttp(new Uri(BaseUri, QuickbloxApiTypes.Session));
                request.Headers.Add("QuickBlox-REST-API-Version", "0.1.0");
                request.ContentType = "application/json";
                request.Method = "POST";

                var nonce = GenerateNonce();
                var timestamp = GenerateTimeStamp();

                var result = request.Json<SessionResult>(new
                {
                    application_id = ApplicationId,
                    auth_key = AuthKey,
                    timestamp,
                    nonce,
                    signature = GenerateAuthMsg(nonce, timestamp),
                    user = new { login = AdminUserId,/* email = UserEmail,*/ password = UserPassword }
                });

                _session = result.session;
            }
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

        private static String GenerateAuthMsg(string nonce, string timestamp)
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
                messsage.Add("user[login]=" + AdminUserId);
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
    }
}