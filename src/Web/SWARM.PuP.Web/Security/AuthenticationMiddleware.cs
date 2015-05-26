using System;
using System.Diagnostics;
using System.Security.Claims;
using System.Threading.Tasks;
using System.Web.Mvc;
using Microsoft.Owin;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Security
{
    public class AuthenticationMiddleware : OwinMiddleware
    {
        private const string TokenPrefix = "Bearer ";
        private readonly int _tokenPrefixLength = TokenPrefix.Length;
        private readonly IUserService _userService;

        public AuthenticationMiddleware(OwinMiddleware next) : base(next)
        {
            _userService = (IUserService)DependencyResolver.Current.GetService(typeof(IUserService));
        }

        public override Task Invoke(IOwinContext context)
        {
            Authenticate(context);

            return Next.Invoke(context);
        }

        private void Authenticate(object obj)
        {
            IOwinContext context = (IOwinContext)obj;

            string encryptedToken;
            if (context.Request.Headers.ContainsKey("Authorization"))
            {
                string s = context.Request.Headers.Get("Authorization");
                if (s.IsNotNullOrEmpty() && s.StartsWith(TokenPrefix, StringComparison.CurrentCultureIgnoreCase))
                {
                    encryptedToken = s.Substring(_tokenPrefixLength);
                    goto CHECK;
                }
            }

            encryptedToken = context.Request.Cookies["token"];

            CHECK:
            if (encryptedToken.IsNotNullOrWhiteSpace())
            {
                try
                {
                    string json = DataProtector.Unprotect(encryptedToken);
                    var accessToken = json.ToObject<AccessToken>();
                    if (accessToken.ExpirationDateUtc > DateTime.UtcNow)
                    {
                        var user = _userService.GetById(accessToken.Id);

                        context.Authentication.User = new ClaimsPrincipal(new PuPClaimsIdentity(user));
                    }
                }
                catch (Exception ex)
                {
                    //TODO: Better error handler
                    Trace.TraceError(ex.Message);
                }
            }
        }
    }
}