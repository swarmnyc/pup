using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Autofac;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Code.Components
{
    public static class RedditHelper
    {
        public static bool RefreshRedditToken(this PuPUser user, bool force = false)
        {
            SocialMedium medium = user.SocialMedia.FirstOrDefault(x => x.Type == SocialMediumType.Reddit);
            if (force || (medium != null && medium.ExpireAtUtc < DateTime.UtcNow.AddSeconds(100)))
            {
                user.UpdateRedditToken(false, medium.Secret);
            }

            return false;
        }

        public static string GetRedditCaptchaId(this PuPUser user)
        {
            user.RefreshRedditToken();

            HttpWebRequest request = WebRequest.CreateHttp("https://oauth.reddit.com/api/new_captcha");
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.UserAgent = "partyupplayer";
            request.Headers.Add("Authorization", "bearer " + user.SocialMedia.First(x => x.Type == SocialMediumType.Reddit).Token);

            request.Write("api_type=json");

            var result = request.ReadAll().ToObject<dynamic>();

            return result.json.data.iden;
        }

        public static string PostToReddit(PuPUser user, string title, string text, string sr, string captchaId, string captcha)
        {
            user.RefreshRedditToken();

            HttpWebRequest request = WebRequest.CreateHttp("https://oauth.reddit.com/api/submit");
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.UserAgent = "partyupplayer";
            request.Headers.Add("Authorization", "bearer " + user.SocialMedia.First(x => x.Type == SocialMediumType.Reddit).Token);
            
            request.Write(String.Format("text={0}&sr={1}&kind=self&title={2}&iden={3}&captcha={4}", text, sr, title, captchaId, captcha));

            return request.ReadAll();
        }

        public static void UpdateRedditToken(this PuPUser user, bool newToken, string code)
        {
            HttpWebRequest request = WebRequest.CreateHttp(OAuthData.Reddit.AccessTokenUrl);
            request.Method = "POST";
            request.ContentType = "application/x-www-form-urlencoded";
            request.Credentials = new NetworkCredential(OAuthData.Reddit.ConsumerKey, OAuthData.Reddit.ConsumerSecret);
            request.PreAuthenticate = true;

            if (newToken)
            {
                request.Write(String.Format("grant_type=authorization_code&code={0}&redirect_uri={1}", code, HttpUtility.UrlEncode(OAuthData.Reddit.CallBackUrl)));
            }
            else
            {
                request.Write(String.Format("grant_type=refresh_token&refresh_token={0}", code));
            }

            var result = request.ReadAll().ToObject<dynamic>();

            if (result.error != null)
            {
                throw new ApplicationException("Update RedditToken failed for User:" + user + ", Message:" + result.error.ToString());
            }

            SocialMedium medium = new SocialMedium()
            {
                Type = SocialMediumType.Reddit,
                Token = result.access_token,
                Secret = newToken ? result.refresh_token : code,
                ExpireAtUtc = DateTime.UtcNow.AddSeconds((double)result.expires_in),
            };

            Update(user, medium);
        }

        private static void Update(PuPUser user, SocialMedium medium)
        {
            var userService = PuPApplication.Current.Ioc.Resolve<IUserService>();

            if (user.SocialMedia.Contains(medium))
            {
                user.SocialMedia.Remove(medium);
            }

            user.SocialMedia.Add(medium);
            userService.Update(user);
        }
    }
}