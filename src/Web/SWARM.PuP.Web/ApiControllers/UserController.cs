using System;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Security.Claims;
using System.Web.Hosting;
using System.Web.Http;
using System.Web.Http.Filters;
using System.Web.Http.Results;
using MongoDB.Bson;
using MultipartDataMediaFormatter.Infrastructure;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Security;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.ApiControllers
{
    [RoutePrefix("api/User")]
    public class UserController : ApiController
    {
        private readonly IImageService _imageService;
        private readonly IUserService _userService;

        public UserController(IUserService userService, IImageService imageService)
        {
            _userService = userService;
            _imageService = imageService;
        }

        [HttpPost, Route("~/api/Login"), ModelValidate]
        public IHttpActionResult Login(LoginViewModel model)
        {
            string errorMessage = null;
            PuPUser user = null;

            user = _userService.Find(model.Email, model.Password);

            RequestContext.Principal = new ClaimsPrincipal(new PuPClaimsIdentity(user));

            if (user == null)
            {
                errorMessage = ErrorCode.E003NotFound;
            }


            return ResponseMessage(GenerateCurrentUserInfoMessage(user, errorMessage));
        }

        [HttpPost]
        [Route("~/api/ExternalLogin"), ModelValidate]
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

            var response = GenerateCurrentUserInfoMessage(user, "");
            return ResponseMessage(response);
        }

        [HttpPost]
        [Route("~/api/Register"), ModelValidate]
        public ResponseMessageResult Register(RegisterViewModel model)
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
                    Id = ObjectId.GenerateNewId().ToString(),
                    UserName = model.UserName,
                    Email = model.Email,
                    PasswordHash = DataProtector.Hash(model.Password)
                };

                RequestContext.Principal = new ClaimsPrincipal(new PuPClaimsIdentity(user));
                if (model.Portrait != null)
                {
                    user.PortraitUrl = GetUserPortraitUrl(user);

                    var stream = new MemoryStream(model.Portrait.Buffer);
                    _imageService.CreateThumbnailTo(stream,
                        HostingEnvironment.MapPath(user.PortraitUrl));
                }

                user = _userService.Add(user);
            }

            return ResponseMessage(GenerateCurrentUserInfoMessage(user, errorMessage));
        }

        private static string GetUserPortraitUrl(PuPUser user)
        {
            return "~/Content/User/" + user.Id + ".png";
        }

        [Authorize, Route("UpdatePortrait"), HttpPost, ModelValidate]
        public IHttpActionResult UpdatePortrait(FormData formData)
        {
            HttpFile file;
            if (!formData.TryGetValue("portrait", out file))
            {
                return BadRequest(ErrorCode.E001WrongParameter);
            }

            string url = GetUserPortraitUrl(User.Identity.GetPuPUser());
            var stream = new MemoryStream(file.Buffer);
            _imageService.CreateThumbnailTo(stream,
                HostingEnvironment.MapPath(url));

            return Ok(url);
        }

        [Authorize]
        public CurrentUserInfo Get()
        {
            return new CurrentUserInfo(User.Identity.GetPuPUser());
        }

        [Authorize, HttpPost, Route("UserTag"), ModelValidate]
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

        [Authorize, HttpDelete, Route("UserTag/{tagId}"), ModelValidate]
        public IHttpActionResult DeleteUserTag(string tagId)
        {
            var user = User.Identity.GetPuPUser();
            user.Tags.Remove(user.Tags.First(x => x.Id == tagId));
            _userService.Update(user);

            return Ok();
        }

        private HttpResponseMessage GenerateCurrentUserInfoMessage(PuPUser user, string errorMessage)
        {
            var response = Request.CreateResponse(HttpStatusCode.OK);
            var result = new RequestResult<CurrentUserToken>();
            if (user == null)
            {
                result.Success = false;
                result.ErrorMessage = errorMessage;
            }
            else
            {
                var at = new AccessToken(user.Id);

                result.Data = new CurrentUserToken(user)
                {
                    AccessToken = DataProtector.Protect(System.Json.ToJson(at)),
                    ExpiresIn = (long)(at.ExpirationDateUtc - DateTime.UtcNow).TotalMilliseconds
                };

                if (result.Data.PortraitUrl.IsNotNullOrWhiteSpace())
                {
                    result.Data.PortraitUrl = Url.Content(result.Data.PortraitUrl);
                }
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