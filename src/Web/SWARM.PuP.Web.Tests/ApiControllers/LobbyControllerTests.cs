using System;
using System.ComponentModel;
using System.Linq;
using Autofac;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using MongoDB;
using MongoDB.Bson;
using SWARM.PuP.Web.ApiControllers;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.QueryFilters;
using SWARM.PuP.Web.Services;
using IContainer = Autofac.IContainer;

namespace SWARM.PuP.Web.Tests.ApiControllers
{
    [TestClass()]
    public class LobbyControllerTests
    {
        private IContainer container;

        public LobbyControllerTests()
        {
            TestHelper.MockDatabase();
            container = TestHelper.GetContainer();
        }

        [TestMethod()]
        public void LobbyController_Filter_Test()
        {
            LobbyController controller = new LobbyController(container.Resolve<ILobbyService>());
            var result = controller.Get(new LobbyFilter()
            {
                Order = "Name",
                PageSize = 1,
                OrderDirection = ListSortDirection.Descending,
                PlayStyles = new[] { PlayStyle.Casual }
            });

            Console.WriteLine(result.ToMongoQueryText());
            Console.WriteLine(result.ToJson());

            Assert.IsTrue(result.Any());
        }
    }
}