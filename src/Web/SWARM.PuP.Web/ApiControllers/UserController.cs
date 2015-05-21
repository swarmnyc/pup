using System;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Web.Http;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Security;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/User")]
    public class UserController : ApiController
    {
        private readonly IUserService _userService;

        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        [HttpPost]
        [Route("~/api/Login")]
        public IHttpActionResult Login(LoginViewModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest("No matches parameters");
            }

            var user = _userService.Find(model.Email, model.Password);

            if (user == null)
            {
                return BadRequest("Email or Password aren't correct");
            }

            var response = GenerateTokenMessage(user);
            return ResponseMessage(response);
        }

        [HttpPost]
        [Route("~/api/ExternalLogin")]
        public IHttpActionResult ExternalLogin(ExternalLoginViewModel model)
        {
            if (ModelState.IsValid)
            {
                return BadRequest("No matches parameters");
            }

            PuPUser user;
            model.Provider = model.Provider.ToLower();
            switch (model.Provider)
            {
                case "google":
                    var request =
                        WebRequest.Create("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + model.Token);
                    var googleUser = request.Json<GoogleUserInfo>();

                    user = _userService.FindByEmail(model.Email);

                    if (user == null)
                    {
                        /*user = CreateUserByExternalLogin(new UserRegisterViewModel
                        {
                            IdFromProvider = googleUser.id,
                            UserName = googleUser.name,
                            PictureUrl = googleUser.picture,
                            Provider = model.Provider,
                            Email = model.Email
                        });*/
                    }
                    break;
#if DEBUG
                case "test":
                    user = _userService.FindByEmail(model.Email);
                    break;
#endif
                default:
                    return BadRequest();
            }

            if (user == null)
            {
                return BadRequest();
            }

            var response = GenerateTokenMessage(user);
            return ResponseMessage(response);
        }

        [Authorize]
        public UserInfoViewModel Get()
        {
            return new UserInfoViewModel(User.Identity.GetPuPUser());
        }

        [AllowAnonymous]
        [HttpPost]
        public IHttpActionResult Register(RegisterViewModel model)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (_userService.FindByEmail(model.Email) != null)
            {
                return BadRequest("Exist");
            }

            var user = new PuPUser
            {
                UserName = model.UserName,
                Email = model.Email,
                PasswordHash = DataProtector.Hash(model.Password)
            };

            var response = GenerateTokenMessage(_userService.Add(user));
            return ResponseMessage(response);
        }

        [Authorize, HttpPost, Route("UserTag")]
        public IHttpActionResult AddUserTag([FromBody] PuPTag tag)
        {
            if (tag == null)
            {
                return BadRequest();
            }

            var user = User.Identity.GetPuPUser();
            user.Tags.Add(tag);
            _userService.Update(user);

            return Ok(tag.Id);
        }

        [Authorize, HttpDelete, Route("UserTag/{tagId}")]
        public IHttpActionResult DeleteUserTag(string tagId)
        {
            var user = User.Identity.GetPuPUser();
            user.Tags.Remove(user.Tags.First(x => x.Id == tagId));
            _userService.Update(user);

            return Ok();
        }

        private HttpResponseMessage GenerateTokenMessage(PuPUser user)
        {
            var at = new AccessToken(user.Id);

            var response = Request.CreateResponse(HttpStatusCode.OK);

            response.Content = new JsonContent(new UserLoggedInViewModel(user)
            {
                AccessToken = DataProtector.Protect(at.ToJson()),
                TokenType = "bearer",
                ExpiresIn = (at.ExpirationDateUtc - DateTime.UtcNow).TotalSeconds
            });

            response.Headers.CacheControl = new CacheControlHeaderValue
            {
                NoCache = true
            };

            return response;
        }
    }
}