using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace SWARM.PuP.Web.Controllers
{
    public class LobbyController : Controller
    {
        // GET: Lobby
        public ActionResult Index(string lobbyId)
        {
            ViewData["LobbyId"] = lobbyId;
            return View();
        }
    }
}