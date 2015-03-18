﻿using System;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web.Http;
using Microsoft.AspNet.Identity;
using Microsoft.Owin.Security;
using Microsoft.Owin.Security.OAuth;
using SWARM.PuP.Web.Auth;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/Account")]
    public class AccountController : ApiController
    {
        //Login used the default owin handler which is setted in ~/App_Start/Startup.Auth.cs

        [HttpPost]
        [Route("~/api/ExternalLogin")]
        public async Task<IHttpActionResult> ExternalLogin(LoginOrSignupViewModel model)
        {
            if (model == null || string.IsNullOrWhiteSpace(model.Email) || string.IsNullOrWhiteSpace(model.Provider) ||
                string.IsNullOrWhiteSpace(model.Token))
            {
                return BadRequest("No matches parameters");
            }

            PuPUser user = null;
            model.Provider = model.Provider.ToLower();
            switch (model.Provider)
            {
                case "google":
                    var request =
                        WebRequest.Create("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + model.Token);
                    var googleUser = request.Json<GoogleUserInfo>();

                    //TODO: if password users want to change external login they can't.
                    //Now the idea is if pass the google auth, then we assume the email is vaild 
                    user = await PuPUserManager.Instance.FindByEmailAsync(model.Email);

                    if (user == null)
                    {
                        user = await CreateUser(new UserRegisterViewModel
                        {
                            IdFromProvider = googleUser.id,
                            UserName = googleUser.name,
                            PictureUrl = googleUser.picture,
                            Provider = model.Provider,
                            Email = model.Email
                        });
                    }
                    break;
#if DEBUG
                case "test":
                    user = await PuPUserManager.Instance.FindByEmailAsync(model.Email);
                    break;
#endif
                default:
                    return BadRequest();
            }

            if (user == null)
            {
                return BadRequest();
            }
            var owin = ActionContext.Request.GetOwinContext();
            var oAuthIdentity =
                await PuPUserManager.Instance.CreateIdentityAsync(user, OAuthDefaults.AuthenticationType);

            var properties = PuPOAuthProvider.CreateProperties(user);
            var ticket = new AuthenticationTicket(oAuthIdentity, properties);
            ticket.Properties.ExpiresUtc = new DateTimeOffset(DateTime.Now.AddMilliseconds(Startup.OAuthOptions.AccessTokenExpireTimeSpan.Milliseconds));

            var accessToken = Startup.OAuthOptions.AccessTokenFormat.Protect(ticket);

            var response = Request.CreateResponse(HttpStatusCode.OK);

            response.Content = new JsonContent(new
            {
                userId = user.Id,
                access_token = accessToken,
                token_type = "bearer",
                expire_in = Startup.OAuthOptions.AccessTokenExpireTimeSpan.TotalMilliseconds
            });

            response.Headers.CacheControl = new CacheControlHeaderValue
            {
                NoCache = true
            };
            return ResponseMessage(response);
        }

        private static async Task<PuPUser> CreateUser(UserRegisterViewModel userRegisterViewModel)
        {
            var pupUser = new PuPUser
            {
                Email = userRegisterViewModel.Email,
                UserName = userRegisterViewModel.Email,
                DisplayName = userRegisterViewModel.UserName,
                PictureUrl = userRegisterViewModel.PictureUrl
            };

            pupUser.AddLogin(new UserLoginInfo(userRegisterViewModel.Provider, userRegisterViewModel.IdFromProvider));

            var result = await PuPUserManager.Instance.CreateAsync(pupUser);

            return result.Succeeded ? pupUser : null;
        }
    }

    public class LoginOrSignupViewModel
    {
        public string Token { get; set; }
        public string Provider { get; set; }
        public string Email { get; set; }
    }

    public class GoogleUserInfo
    {
        public string id;
        public string name;
        public string picture;
    }

    public class UserRegisterViewModel
    {
        public string UserName { get; set; }
        public string PictureUrl { get; set; }
        public string IdFromProvider { get; set; }
        public string Provider { get; set; }
        public string Email { get; set; }
    }
}