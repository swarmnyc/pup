using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web
{
    public class OAuthData
    {
        public static OAuthData Tumblr = new OAuthData()
        {
            Type = SocialMediumType.Tumblr,
            ConsumerKey = "LjsDKGXRkYdiRkVP9BRUCRhKRUkFnfT9MD7gUYZg6rHjseDJpB",
            ConsumerSecret = "Esiseof9mPJ2cTaXH9lmd1pyvuUOmmEi5JMf5qrrP0SZh2rV4q",
            RequestTokenUrl = "https://www.tumblr.com/oauth/request_token",
            AuthorizeUrl = "https://www.tumblr.com/oauth/authorize",
            AccessTokenUrl = "https://www.tumblr.com/oauth/access_token",
            UserInfo = "http://api.tumblr.com/v2/user/info",
            CallBackUrl = "/OAuth/TumblrCallback"
        };


        public static OAuthData Twitter = new OAuthData()
        {
            Type = SocialMediumType.Twitter,
            ConsumerKey = "tuPdqGWSqvDNRC8TrcJ1dyuSd",
            ConsumerSecret = "oAjcN1hXSo0AZw9XauXU6qbwcR5FDBYnAvSHygFKbE2wg9kcxs",
            RequestTokenUrl = "https://api.twitter.com/oauth/request_token",
            AuthorizeUrl = "https://api.twitter.com/oauth/authorize",
            AccessTokenUrl = "https://api.twitter.com/oauth/access_token",
            UserInfo = "https://api.twitter.com/1.1/account/settings.json",
            CallBackUrl = "/OAuth/TwitterCallback"
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