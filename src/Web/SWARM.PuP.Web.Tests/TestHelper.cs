using System;
using Autofac;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace SWARM.PuP.Web.Tests
{
    public static class TestHelper
    {
        public static IContainer GetContainer(Action<ContainerBuilder> extraBinding = null)
        {
            ContainerBuilder builder = new ContainerBuilder();
            builder.RegisterAssemblyTypes(typeof(PuPUser).Assembly).Where(x => x.FullName.EndsWith("Service")).AsImplementedInterfaces();
            if (extraBinding != null)
            {
                extraBinding(builder);
            }

            return builder.Build();
        }
    }
}