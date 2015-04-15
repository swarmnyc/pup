using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Security.Claims;
using System.Security.Principal;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using MongoDB.Bson;
using Moq;
using Newtonsoft.Json;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;
using IContainer = Autofac.IContainer;

namespace SWARM.PuP.Web.Tests.ApiControllers
{
    [TestClass()]
    public class UserControllerTests
    {
        private readonly IContainer container;

        public UserControllerTests()
        {
            TestHelper.MockDatabase();
            container = TestHelper.GetContainer();
        }

        [TestMethod()]
        public void UserController_Create_Test()
        {
            UserController controller = container.Resolve<UserController>();
            controller.Register(new ViewModels.RegisterViewModel() {
                Email= "wade@swarmnyc.com",
                Password="Abc123456",
                ConfirmPassword = "Abc123456",
            });
        }       
    }
}