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
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/User")]
    public class UserController : ApiController
    {
        [HttpPost]
        [Route("~/api/Login")]
        public IHttpActionResult Login(LoginViewModel model)
        {
            if (model == null || string.IsNullOrWhiteSpace(model.Email) || string.IsNullOrWhiteSpace(model.Password))
            {
                return BadRequest("No matches parameters");
            }

            PuPUser user = PuPUserManager.Instance.Find(model.Email, model.Password);

            if (user == null)
            {
                return BadRequest();
            }

            HttpResponseMessage response = GenerateTokenResponse(user);
            return ResponseMessage(response);
        }


        [HttpPost]
        [Route("~/api/ExternalLogin")]
        public IHttpActionResult ExternalLogin(ExternalLoginViewModel model)
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

                    user = PuPUserManager.Instance.FindByEmail(model.Email);

                    if (user == null)
                    {
                        user = CreateUserByExternalLogin(new UserRegisterViewModel
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
                    user = PuPUserManager.Instance.FindByEmail(model.Email);
                    break;
#endif
                default:
                    return BadRequest();
            }

            if (user == null)
            {
                return BadRequest();
            }

            HttpResponseMessage response = GenerateTokenResponse(user);
            return ResponseMessage(response);
        }


        [Authorize]
        public async Task<UserInfoViewModel> Get()
        {
            var user = await PuPUserManager.Instance.FindByNameAsync(User.Identity.Name);

            return new UserInfoViewModel(user);
        }

        [AllowAnonymous]
        [Route("Register")]
        public IHttpActionResult Register(RegisterViewModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            var user = new PuPUser() { UserName = model.Email, Email = model.Email };

            IdentityResult result = PuPUserManager.Instance.Create(user, model.Password);

            if (!result.Succeeded)
            {
                return BadRequest(result.Errors.FirstOrDefault());
            }

            HttpResponseMessage response = GenerateTokenResponse(user);
            return ResponseMessage(response);
        }

        [Authorize, HttpPost, Route("UserTag")]
        public async Task<IHttpActionResult> AddUserTag([FromBody]PuPTag tag)
        {
            if (tag == null)
            {
                return BadRequest();
            }

            var user = await PuPUserManager.Instance.FindByIdAsync(User.Identity.GetUserId());
            user.Tags.Add(tag);
            await PuPUserManager.Instance.UpdateAsync(user);

            return Ok(tag.Id);
        }

        [Authorize, HttpDelete, Route("UserTag/{tagId}")]
        public async Task<IHttpActionResult> DeleteUserTag(string tagId)
        {
            var user = await PuPUserManager.Instance.FindByNameAsync(User.Identity.Name);
            user.Tags.Remove(user.Tags.First(x => x.Id == tagId));
            await PuPUserManager.Instance.UpdateAsync(user);

            return Ok();
        }

        private HttpResponseMessage GenerateTokenResponse(PuPUser user)
        {
            var owin = ActionContext.Request.GetOwinContext();
            var oAuthIdentity = PuPUserManager.Instance.CreateIdentity(user, OAuthDefaults.AuthenticationType);

            var properties = PuPOAuthProvider.CreateProperties(user);
            var ticket = new AuthenticationTicket(oAuthIdentity, properties);
            ticket.Properties.ExpiresUtc = new DateTimeOffset(DateTime.Now.AddMilliseconds(Startup.OAuthOptions.AccessTokenExpireTimeSpan.TotalMilliseconds));

            var accessToken = Startup.OAuthOptions.AccessTokenFormat.Protect(ticket);

            var response = Request.CreateResponse(HttpStatusCode.OK);

            response.Content = new JsonContent(new UserLoggedInViewModel(user)
            {
                AccessToken = accessToken,
                TokenType = "bearer",
                ExpiresIn = (long)Startup.OAuthOptions.AccessTokenExpireTimeSpan.TotalMilliseconds
            });

            response.Headers.CacheControl = new CacheControlHeaderValue
            {
                NoCache = true
            };

            return response;
        }


        private static PuPUser CreateUserByExternalLogin(UserRegisterViewModel userRegisterViewModel)
        {
            var pupUser = new PuPUser
            {
                Email = userRegisterViewModel.Email,
                UserName = userRegisterViewModel.Email,
                DisplayName = userRegisterViewModel.UserName,
                PictureUrl = userRegisterViewModel.PictureUrl
            };

            pupUser.AddLogin(new UserLoginInfo(userRegisterViewModel.Provider, userRegisterViewModel.IdFromProvider));

            var result = PuPUserManager.Instance.Create(pupUser);

            return result.Succeeded ? pupUser : null;
        }
    }


}