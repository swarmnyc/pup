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

        [HttpPost, Route("~/api/Login")]
        public IHttpActionResult Login(LoginViewModel model)
        {
            string errorMessage = null;
            PuPUser user = null;

            user = _userService.Find(model.Email, model.Password);

            if (user == null)
            {
                errorMessage = ErrorCode.E003NotFound;
            }


            return ResponseMessage(GenerateUserRequestMessage(user, errorMessage));
        }

        [HttpPost]
        [Route("~/api/ExternalLogin")]
        public IHttpActionResult ExternalLogin(ExternalLoginViewModel model)
        {
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

            var response = GenerateUserRequestMessage(user, "");
            return ResponseMessage(response);
        }

        [HttpPost]
        [Route("~/api/Register")]
        public IHttpActionResult Register(RegisterViewModel model)
        {
            string errorMessage = null;
            PuPUser user = null;
            if (_userService.CheckExist(model.Email, model.UserName))
            {
                errorMessage = ErrorCode.E002Exist;
            }
            else
            {
                user = new PuPUser
                {
                    UserName = model.UserName,
                    Email = model.Email,
                    PasswordHash = DataProtector.Hash(model.Password)
                };

                user = _userService.Add(user);
            }

            return ResponseMessage(GenerateUserRequestMessage(user, errorMessage));
        }

        [Authorize]
        public UserInfoViewModel Get()
        {
            return new UserInfoViewModel(User.Identity.GetPuPUser());
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

        private HttpResponseMessage GenerateUserRequestMessage(PuPUser user, string errorMessage)
        {
            var response = Request.CreateResponse(HttpStatusCode.OK);
            var result = new RequestResult<UserRequestViewModel>();
            if (user == null)
            {
                result.Success = false;
                result.ErrorMessage = errorMessage;
            }
            else
            {
                var at = new AccessToken(user.Id);

                result.Data = new UserRequestViewModel(user)
                {
                    AccessToken = DataProtector.Protect(at.ToJson()),
                    ExpiresIn = (long)(at.ExpirationDateUtc - DateTime.UtcNow).TotalSeconds
                };

                result.Success = true;
            }
            response.Content = new JsonContent(result);

            response.Headers.CacheControl = new CacheControlHeaderValue
            {
                NoCache = true
            };

            return response;
        }
    }
}