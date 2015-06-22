using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Code.Components;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Code.Controllers
{
    public class RedditController : Controller
    {
        private readonly ILobbyService _lobbyService;
        private readonly IGameService _gameService;

        public RedditController(ILobbyService lobbyService, IGameService gameService)
        {
            _lobbyService = lobbyService;
            _gameService = gameService;
        }

        [Authorize]
        public ActionResult Share(string lobbyId)
        {
            ViewBag.CaptchaId = RedditHelper.GetRedditCaptchaId(User.Identity.GetPuPUser());

            return View();
        }

        [Authorize, HttpPost]
        public ActionResult Share(RedditShareViewModel model)
        {
            if (ModelState.IsValid)
            {
                var lobby = _lobbyService.GetById(model.LobbyId);
                var game = _gameService.GetById(lobby.GameId);
                var sr = game.GetTagValue("RedditSR");
                if (string.IsNullOrWhiteSpace(sr))
                {
                    Trace.TraceWarning("{0} doesn't have SubRiddit tag", game.Name);
                    return RedirectToAction("Done");
                }

                string result = ShareHelper.ShareToReddit(User.Identity.GetPuPUser(), lobby, sr, model.CaptchaId, model.Captcha,
                model.LocalTime);

                if (result.Contains("BAD_CAPTCHA"))
                {
                    ViewBag.ErrorMessage = "Captcha was incorrect";
                    ViewBag.CaptchaId = RedditHelper.GetRedditCaptchaId(User.Identity.GetPuPUser());
                    return View();
                }
                else if (result.Contains(".error.RATELIMIT"))
                {
                    ViewBag.ErrorMessage = "you are doing that too much. try again late.";
                    ViewBag.CaptchaId = RedditHelper.GetRedditCaptchaId(User.Identity.GetPuPUser());
                    return View();
                }
                else
                {
                    Trace.WriteLine("Reddit Share Result: " + result);

                    return RedirectToAction("Done");
                }

            }
            else
            {
                ViewBag.ErrorMessage = ErrorCode.E001WrongParameter;
                return View();
            }
        }

        public ActionResult Done()
        {
            return Content("");
        }
    }

    public class RedditShareViewModel
    {
        [Required]
        public string LobbyId { get; set; }

        [Required]
        public string LocalTime { get; set; }

        [Required]
        public string CaptchaId { get; set; }

        [Required]
        public string Captcha { get; set; }
    }
}