using Microsoft.Owin;
using Owin;
using SWARM.PuP.Web;
using SWARM.PuP.Web.Security;

[assembly: OwinStartup(typeof (Startup))]

namespace SWARM.PuP.Web
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {            
            app.Use<AuthenticationMiddleware>();
        }
    }
}