using SWARM.PuP.Web.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace SWARM.PuP.Web.Code.Controllers
{
    public class UserController : Controller
    {
        private readonly IUserService _userService;

        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        public ActionResult Index(string name)
        {
            var user = _userService.GetSingle(x => x.UserName.ToLower() == name.ToLower());
            if (user == null)
            {
                return Redirect("~/404.shtml");
            }

            ViewData["userId"] = user.Id;
            ViewData["userPortraitUrl"] = user.PortraitUrl;
            ViewData["userName"] = name;

            return View();
        }
    }
}