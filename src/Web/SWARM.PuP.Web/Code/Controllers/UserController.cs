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
        public ActionResult Index(string name)
        {   
            return View();
        }
    }
}