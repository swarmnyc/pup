﻿using System;
using System.IO;
using System.Net;
using System.Web;
using System.Web.Mvc;
using SWARM.PuP.Web.Code.Components;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;
using TwitterOAuth.Enum;
using TwitterOAuth.Impl;

namespace SWARM.PuP.Web.Controllers
{
    public class OAuthController : Controller
    {
        [Authorize]
        public ActionResult Tumblr()
        {
            return Authorize(OAuthData.Tumblr);
        }

        [Authorize]
        public ActionResult TumblrCallback()
        {
            return Callback(OAuthData.Tumblr, x => x.ToObject<dynamic>().response.user.name);
        }

        [Authorize]
        public ActionResult Twitter()
        {
            return Authorize(OAuthData.Twitter);
        }



        [Authorize]
        public ActionResult TwitterCallback()
        {
            return Callback(OAuthData.Twitter, x => x.ToObject<dynamic>().screen_name);
        }

        [Authorize]
        public ActionResult Reddit()
        {
            EnsureCookie();

            return Redirect(string.Format("{0}?client_id={1}&state=oauth&response_type=code&duration=permanent&scope=identity,submit&redirect_uri={2}",
                OAuthData.Reddit.AuthorizeUrl, OAuthData.Reddit.ConsumerKey, Url.Encode(OAuthData.Reddit.CallBackUrl)));
        }

        [Authorize]
        public ActionResult RedditCallback()
        {
            string code = Request.QueryString["code"];

            User.Identity.GetPuPUser().UpdateRedditToken(true, code);

            return RedirectToAction("Done");
        }


        public ActionResult Done()
        {
            return Content("");
        }

        private ActionResult Authorize(OAuthData data)
        {
            EnsureCookie();

            var authRequest = new TwitterOAuthClient
            {
                ConsumerKey = data.ConsumerKey,
                ConsumerSecret = data.ConsumerSecret,
                CallBackUrl = Request.Url.GetLeftPart(UriPartial.Authority) + data.CallBackUrl
            };

            string authUrl = null;
            var query = authRequest.OAuthWebRequest(RequestMethod.POST, data.RequestTokenUrl, string.Empty);

            if (query.Length > 0)
            {
                var nameValueCollection = HttpUtility.ParseQueryString(query);
                if (nameValueCollection["oauth_callback_confirmed"] != null &&
                    nameValueCollection["oauth_callback_confirmed"] != "true")
                    throw new Exception("OAuth callback not confirmed.");

                authUrl = data.AuthorizeUrl + "?oauth_token=" + nameValueCollection["oauth_token"];
                Response.Cookies["oauth_secret"].Value = nameValueCollection["oauth_token_secret"];
            }

            return Redirect(authUrl);
        }

        private ActionResult Callback(OAuthData data, Func<string, string> getUserId)
        {
            var authRequest = new TwitterOAuthClient
            {
                ConsumerKey = data.ConsumerKey,
                ConsumerSecret = data.ConsumerSecret,
                Token = Request["oauth_token"],
                TokenSecret = Request.Cookies["oauth_secret"].Value,
                OAuthVerifier = Request["oauth_verifier"]
            };

            var query = authRequest.OAuthWebRequest(RequestMethod.POST, data.AccessTokenUrl, string.Empty);

            if (query.Length > 0)
            {
                var nameValueCollection = HttpUtility.ParseQueryString(query);


                SocialMedium medium = new SocialMedium()
                {
                    Type = data.Type,
                    Token = nameValueCollection["oauth_token"],
                    Secret = nameValueCollection["oauth_token_secret"],
                    ExpireAtUtc = DateTime.UtcNow.AddYears(10),
                };

                authRequest = new TwitterOAuthClient
                {
                    ConsumerKey = data.ConsumerKey,
                    ConsumerSecret = data.ConsumerSecret,
                    Token = medium.Token,
                    TokenSecret = medium.Secret
                };

                query = authRequest.OAuthWebRequest(RequestMethod.GET, data.UserInfo, string.Empty);
                medium.UserId = getUserId(query);

                UpdateMedium(medium);
            }

            return RedirectToAction("Done");
        }

        private void UpdateMedium(SocialMedium medium)
        {
            var userService = Resolver.GetService<IUserService>();
            var user = User.Identity.GetPuPUser();

            if (user.SocialMedia.Contains(medium))
            {
                user.SocialMedia.Remove(medium);
            }

            user.SocialMedia.Add(medium);
            userService.Update(user);
        }

        private void EnsureCookie()
        {
            if (!string.IsNullOrWhiteSpace(Request.QueryString["user_token"]))
            {
                Response.Cookies["token"].Value = Request.QueryString["user_token"];
            }
        }

    }
}