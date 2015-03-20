using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;

namespace SWARM.PuP.Web.Services.Quickblox
{
    internal static class QuickbloxHttpHelper
    {
		//TODO: Move to config
        private const string ApplicationId = "20103";
        private const string AuthKey = "gXa5zW9K5wdTuCV";
        private const string AuthSecret = "YGabpebTkW2Q9r6";
        private const int TimeOut = 19;
        
        private static readonly object LockObj = new object();
        private static readonly Random Random = new Random();
        private static readonly Uri BaseUri = new Uri("https://api.quickblox.com/");
        private static Session _session;
        private static readonly string User = "WadeHuang";
        private static readonly string UserEmail = "wade@swarmnyc.com";
        private static readonly string UserPassword = "swarmnyc";

        internal static WebRequest Create(string api, string method)
        {
            if (IsNoSession())
            {
                InitSession();
            }

            WebRequest request = WebRequest.CreateHttp(new Uri(BaseUri, api));
            request.Headers.Add("QB-Token", _session.token);
            request.Headers.Add("QuickBlox-REST-API-Version", "0.1.0");
            request.ContentType = "application/json";
            request.Method = method;
            return request;
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
                    user = new {login = User, email = UserEmail, password = UserPassword}
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

            if (User != null)
            {
                messsage.Add("user[login]=" + User);
                messsage.Add("user[email]=" + UserEmail);
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