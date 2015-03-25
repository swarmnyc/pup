using System;
using System.Linq;
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
    [RoutePrefix("api/User")]
    public class UserController : ApiController
    {
        //Login used the default owin handler which is setted in ~/App_Start/Startup.Auth.cs

        [HttpPost]
        [Route("~/api/ExternalLogin")]
        public async Task<IHttpActionResult> ExternalLogin(ExternalLoginViewModel model)
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
            ticket.Properties.ExpiresUtc = new DateTimeOffset(DateTime.Now.AddMilliseconds(Startup.OAuthOptions.AccessTokenExpireTimeSpan.TotalMilliseconds));

            var accessToken = Startup.OAuthOptions.AccessTokenFormat.Protect(ticket);

            var response = Request.CreateResponse(HttpStatusCode.OK);

            response.Content = new JsonContent(new
            {
                userId = user.Id,
                access_token = accessToken,
                token_type = "bearer",
                expires_in = Startup.OAuthOptions.AccessTokenExpireTimeSpan.TotalMilliseconds
            });

            response.Headers.CacheControl = new CacheControlHeaderValue
            {
                NoCache = true
            };
            return ResponseMessage(response);
        }

        [Authorize]
        public async Task<UserInfoViewModel> Get()
        {
            var user = await PuPUserManager.Instance.FindByNameAsync(User.Identity.Name);

            return new UserInfoViewModel(user);
        }

        [Authorize,HttpDelete, Route("UserTag")]
        public async Task<IHttpActionResult> UserTag(string tagId)
        {
            var user = await PuPUserManager.Instance.FindByNameAsync(User.Identity.Name);
            user.Tags.Remove(user.Tags.First(x => x.Id == tagId));
            await PuPUserManager.Instance.UpdateAsync(user);

            return Ok();
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
}