using System;
using System.Collections.Generic;
using System.Reflection;
using System.Web;
using System.Web.Http;
using System.Web.Mvc;
using System.Web.Optimization;
using System.Web.Routing;
using Autofac;
using Autofac.Integration.Mvc;
using Autofac.Integration.WebApi;

namespace SWARM.PuP.Web
{
    public class PuPApplication : System.Web.HttpApplication
    {

        public PuPApplication()
        {
            Current = this;
            Ioc = RegisterDependencies();
        }

        public static PuPApplication Current { get; private set; }

        public IContainer Ioc { get; private set; }
    
        protected void Application_Start()
        {
            AreaRegistration.RegisterAllAreas();
            GlobalConfiguration.Configure(WebApiConfig.Register);
            FilterConfig.RegisterGlobalFilters(GlobalFilters.Filters);
            RouteConfig.RegisterRoutes(RouteTable.Routes);

            GlobalConfiguration.Configuration.DependencyResolver = new AutofacWebApiDependencyResolver(Ioc);
            DependencyResolver.SetResolver(new AutofacDependencyResolver(Ioc));
        }

        private IContainer RegisterDependencies()
        {
            ContainerBuilder builder = new ContainerBuilder();

            Assembly assembly = Assembly.GetExecutingAssembly();
            builder.RegisterApiControllers(assembly);
            builder.RegisterControllers(assembly);

            builder.RegisterAssemblyTypes(typeof(Startup).Assembly).Where(x => x.Name.EndsWith("Service"))
                   .AsImplementedInterfaces()
                   .InstancePerLifetimeScope();

            return builder.Build();
        }
    }
}
