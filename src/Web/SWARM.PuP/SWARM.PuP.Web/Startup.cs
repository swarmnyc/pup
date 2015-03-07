using System.Reflection;
using System.Web.Http;
using Autofac;
using Autofac.Integration.WebApi;
using DotNetDoodle.Owin;
using DotNetDoodle.Owin.Dependencies.Autofac;

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
            app.UseAutofacContainer(RegisterServices())
               .UseWebApiWithContainer(GlobalConfiguration.Configuration);
            ConfigureAuth(app);
        }

        public IContainer RegisterServices()
        {
            ContainerBuilder builder = new ContainerBuilder();

            builder.RegisterApiControllers(Assembly.GetExecutingAssembly());
            builder.RegisterOwinApplicationContainer();

            builder.RegisterType<QuickbloxChatService>()
                   .As<IChatService>()
                   .InstancePerLifetimeScope();

            return builder.Build();
        }
    }
}
