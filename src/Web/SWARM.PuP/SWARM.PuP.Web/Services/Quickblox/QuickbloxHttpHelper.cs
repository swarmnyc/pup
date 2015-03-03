using System;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace SWARM.PuP.Web.Services.Quickblox
{
    internal static class QuickbloxHttpHelper
    {
        //TODO: Move to config
        private static readonly object LockObj = new object();
        private static readonly Random Random = new Random();
        private static readonly Uri BaseUri = new Uri("https://api.quickblox.com/");
        private static Session _session;
        private const string ApplicationId = "20103";
        private const string AuthKey = "gXa5zW9K5wdTuCV";
        private const string AuthSecret = "YGabpebTkW2Q9r6";

        private const int TimeOut = 19;

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

                WebRequest request = WebRequest.CreateHttp(new Uri(BaseUri, QuickbloxRequestTypes.CreateSession));
                request.Headers.Add("QuickBlox-REST-API-Version", "0.1.0");
                request.ContentType = "application/json";
                request.Method = "POST";

                string nonce = GenerateNonce();
                string timestamp = GenerateTimeStamp();

                request.Write(new
                {
                    application_id = "20103",
                    auth_key = "gXa5zW9K5wdTuCV",
                    timestamp = timestamp,
                    nonce = nonce,
                    signature = GenerateAuthMsg(nonce, timestamp)
                });

                var response = request.GetResponse();
                
                var result = response.Read<SessionResult>();

                _session = result.session;
            }
        }

        private static string GenerateTimeStamp()
        {
            TimeSpan ts = DateTime.UtcNow - new DateTime(1970, 1, 1, 0, 0, 0, 0);
            return Convert.ToInt64(ts.TotalSeconds).ToString();
        }

        private static string GenerateNonce()
        {
            return Random.Next(123400, 9999999).ToString();
        }

        private static String GenerateAuthMsg(string nonce, string timestamp)
        {

            String[] messsage = {
                "application_id=" + ApplicationId,
                "auth_key=" + AuthKey,
                "nonce=" + nonce,
                "timestamp=" + timestamp,
            };
            string mes = string.Join("&", messsage.OrderBy(m => m).ToArray());

            StringBuilder sb = new StringBuilder();
            using (HMACSHA1 hmac = new HMACSHA1(Encoding.ASCII.GetBytes(AuthSecret)))
            {
                byte[] hash = hmac.ComputeHash(Encoding.ASCII.GetBytes(mes));

                foreach (var b in hash)
                {
                    sb.Append(b.ToString("x2"));
                }
            }

            return sb.ToString();
        }
    }
}