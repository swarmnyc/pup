using System;
using System.Globalization;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Security;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.ViewModels;

namespace SWARM.PuP.Web.Controllers
{
    [Authorize]
    public class AccountController : Controller
    {
        private readonly IUserService userService;

        public AccountController(IUserService service)
        {
            this.userService = service;
        }

        //
        // GET: /Account/Login
        [AllowAnonymous]
        public ActionResult Login(string returnUrl)
        {
            ViewBag.ReturnUrl = returnUrl;
            return View();
        }

        //
        // POST: /Account/Login
        [HttpPost]
        [AllowAnonymous]
        [ValidateAntiForgeryToken]
        public ActionResult Login(LoginViewModel model, string returnUrl)
        {
            if (!ModelState.IsValid)
            {
                return View(model);
            }

            // Todo: Better code;
            string pw = DataProtector.Hash(model.Password);
            var user = userService.Find(model.Email, model.Password);

            if (user == null)
            {
                ModelState.AddModelError("", "Invalid login attempt.");
                return View(model);
            }
            else
            {
                AccessToken at = new AccessToken(user.Id);
                this.Request.GetOwinContext().Authentication.User = new ClaimsPrincipal(new PuPClaimsIdentity(user));
                this.Response.Cookies.Set(new HttpCookie("token", DataProtector.Protect(at.ToJson()))
                {
                    Expires = at.ExpirationDateUtc
                });

                return Redirect(returnUrl ?? "~/");
            }

        }
    }
}