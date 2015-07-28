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
        private IUserService _userService;        

        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        public ActionResult Index(string name)
        {           
            ViewData["UserId"] = _userService.GetSingle(x=>x.UserName.ToLower() == name.ToLower()).Id;
            return View();
        }
    }
}