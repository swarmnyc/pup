using Microsoft.Owin;
using Owin;

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
