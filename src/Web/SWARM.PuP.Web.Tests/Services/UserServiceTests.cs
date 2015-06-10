using System;
using System.Collections.Generic;
using System.Dynamic;
using System.IO;
using Autofac;
using CsvHelper;
using CsvHelper.Configuration;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Tests.Services
{
    [TestClass()]
    public class UserServiceTests
    {
        private readonly IUserService _userService;

        public UserServiceTests()
        {
            //TestHelper.MockDatabase();
            IContainer ioc = TestHelper.GetContainer();
            _userService = ioc.Resolve<IUserService>();
        }

        [TestMethod()]
        public void UserService_UpdatePortrait_Test()
        {
            _userService.UpdatePortrait(_userService.GetById("557787f571bec331182a534e"),"abc");
        }
    }
}