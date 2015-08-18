using System.Configuration;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web
{
    public class OAuthData
    {
        public static OAuthData Tumblr = new OAuthData()
        {
            Type = SocialMediumType.Tumblr,
            ConsumerKey = ConfigurationManager.AppSettings["Tumblr_AuthKey"],
            ConsumerSecret = ConfigurationManager.AppSettings["Tumblr_AuthSecret"],
            RequestTokenUrl = "https://www.tumblr.com/oauth/request_token",
            AuthorizeUrl = "https://www.tumblr.com/oauth/authorize",
            AccessTokenUrl = "https://www.tumblr.com/oauth/access_token",
            UserInfo = "http://api.tumblr.com/v2/user/info",
            CallBackUrl = "/OAuth/TumblrCallback"
        };

        public static OAuthData Twitter = new OAuthData()
        {
            Type = SocialMediumType.Twitter,
            ConsumerKey = ConfigurationManager.AppSettings["Twitter_AuthKey"],
            ConsumerSecret = ConfigurationManager.AppSettings["Twitter_AuthSecret"],
            RequestTokenUrl = "https://api.twitter.com/oauth/request_token",
            AuthorizeUrl = "https://api.twitter.com/oauth/authorize",
            AccessTokenUrl = "https://api.twitter.com/oauth/access_token",
            UserInfo = "https://api.twitter.com/1.1/account/settings.json",
            CallBackUrl = "/OAuth/TwitterCallback"
        };

        public static OAuthData Reddit = new OAuthData()
        {
            Type = SocialMediumType.Reddit,
            ConsumerKey = ConfigurationManager.AppSettings["Reddit_AuthKey"],
            ConsumerSecret = ConfigurationManager.AppSettings["Reddit_AuthSecret"],
            AuthorizeUrl = "https://www.reddit.com/api/v1/authorize.compact",
            AccessTokenUrl = "https://www.reddit.com/api/v1/access_token",
            CallBackUrl = ConfigurationManager.AppSettings["Reddit_ApiUrl"]
        };

        public SocialMediumType Type { get; set; }

        public string ConsumerKey { get; set; }
        public string ConsumerSecret { get; set; }
        public string RequestTokenUrl { get; set; }
        public string AuthorizeUrl { get; set; }
        public string AccessTokenUrl { get; set; }
        public string UserInfo { get; set; }
        public string CallBackUrl { get; set; }

    }
}