﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Web.Mvc;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Web;
using SWARM.PuP.Web;
using SWARM.PuP.Web.Controllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services.Quickblox;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass]
    public class QuickbloxChatServiceTest
    {
        [TestMethod]
        public void CreateUserTest()
        {
            try
            {
                QuickbloxChatService s = new QuickbloxChatService();
                var r = s.CreateUser(new PuPUser()
                {
                    UserName = "Test",
                    Email = "Test@swarmnyc.com"
                });

                Console.Write(r);
            }
            catch (WebException e)
            {
                var error = e.Response.ReadAll();
                Console.Write(error);
            }
        }
    }
}
