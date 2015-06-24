using System;
using System.Diagnostics;
using System.Net;
using System.Text;
using System.Web;
using System.Web.Mvc;
using SWARM.PuP.Web.Code.Components;
using SWARM.PuP.Web.Models;
using TwitterOAuth.Enum;
using TwitterOAuth.Impl;

namespace SWARM.PuP.Web
{
    public class ShareHelper
    {
        public static void ShareToTumblur(SocialMedium medium, Lobby lobby, string localTime)
        {
            try
            {
                //TODO: Bug with Chinese letter in AuthRequest
                var msg = HttpUtility.UrlEncode(GetMessage(lobby, localTime), Encoding.UTF8);
                var authRequest = new TwitterOAuthClient
                {
                    ConsumerKey = OAuthData.Tumblr.ConsumerKey,
                    ConsumerSecret = OAuthData.Tumblr.ConsumerSecret,
                    Token = medium.Token,
                    TokenSecret = medium.Secret
                };

                authRequest.OAuthWebRequest(RequestMethod.POST, string.Format("https://api.tumblr.com/v2/blog/{0}.tumblr.com/post", medium.UserId),
                    "type=text&body=" + msg);
            }
            catch (Exception ex)
            {
                Trace.TraceError("Share To Tumblur failed for lobby {0}\r\n{1}", lobby, ex);
            }
        }

        public static void ShareToTwitter(SocialMedium medium, Lobby lobby, string localTime)
        {
            try
            {
                //TODO: Bug with Chinese letter in AuthRequest
                var msg = HttpUtility.UrlEncode(GetMessage(lobby, localTime), Encoding.UTF8);
                var authRequest = new TwitterOAuthClient
                {
                    ConsumerKey = OAuthData.Twitter.ConsumerKey,
                    ConsumerSecret = OAuthData.Twitter.ConsumerSecret,
                    Token = medium.Token,
                    TokenSecret = medium.Secret
                };

                authRequest.OAuthWebRequest(RequestMethod.POST, "https://api.twitter.com/1.1/statuses/update.json",
                    "status=" + msg);
            }
            catch (Exception ex)
            {
                Trace.TraceError("Share To Twitter failed for lobby {0}\r\n{1}", lobby, ex);
            }
        }

        public static void ShareToFacebook(SocialMedium medium, Lobby lobby, string localTime)
        {
            try
            {
                var msg = HttpUtility.UrlEncode(GetMessage(lobby, localTime), Encoding.UTF8);
                var link = HttpUtility.UrlEncode(HttpContext.Current.Request.Url.GetLeftPart(UriPartial.Authority) + ("/lobby/" + lobby.Id));
                var url = string.Format("https://graph.facebook.com/v2.3/me/feed?access_token={0}&message={1}&link={2}",
                    medium.Token, msg, link);
                WebRequest webRequest = WebRequest.CreateHttp(url);
                webRequest.Method = "POST";
                webRequest.ReadAll();
            }
            catch (Exception ex)
            {
                Trace.TraceError("Share To Facebook failed for lobby {0}\r\n{1}", lobby, ex);
            }

        }

        public static string ShareToReddit(PuPUser user, Lobby lobby, string sr, string captchaId, string captcha, string localTime)
        {
            try
            {
                var msg = HttpUtility.UrlEncode(GetMessage(lobby, localTime), Encoding.UTF8);
                var title = HttpUtility.UrlEncode(String.Format("Who's up for {0} this {1}", lobby.Name, localTime), Encoding.UTF8);
                return RedditHelper.PostToReddit(user, title, msg, sr, captchaId, captcha);
            }
            catch (Exception ex)
            {
                Trace.TraceError("Share To Reddit failed for lobby {0}\r\n{1}", lobby, ex);
                return "error";
            }
        }

        public static string GetMessage(Lobby lobby, string localTime)
        {
            return string.Format(@"Who's up for {0} this {1}

PUP: Find gamers to play with http://partyupplayer.com", lobby.Name, localTime);
        }
    }
}