using System;
using System.Diagnostics;
using System.Web.Mvc;
using Autofac.Core;
using Autofac.Integration.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using SWARM.PuP.Web.Code.Components;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Tests
{
    [TestClass]
    public class RedditHelperTest
    {
        public RedditHelperTest()
        {
            PuPApplication pup = new PuPApplication();
        }

        [TestMethod]
        public void RedditHelper_RefreshTokenTest()
        {
            var userService = new UserService(null);
            var user = userService.FindByEmail("Wade@swarmnyc.com");
            user.RefreshRedditToken(true);
        }

        [TestMethod]
        public void RedditHelper_GetRedditCaptchaIdTest()
        {
            var userService = new UserService(null);
            var user = userService.FindByEmail("Wade@swarmnyc.com");
            Trace.Write(user.GetRedditCaptchaId());
        }
    }
}
