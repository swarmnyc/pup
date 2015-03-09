using System.Reflection;
using System.Web.Http;
using System.Web.Mvc;
using Autofac;
using Autofac.Integration.Mvc;
using Autofac.Integration.WebApi;

using Microsoft.Owin;
using Owin;
using SWARM.PuP.Web.Services;
using SWARM.PuP.Web.Services.Quickblox;

[assembly: OwinStartupAttribute(typeof(SWARM.PuP.Web.Startup))]
namespace SWARM.PuP.Web
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
