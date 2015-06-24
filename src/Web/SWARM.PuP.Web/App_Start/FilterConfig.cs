using System.Web;
using System.Web.Mvc;

namespace SWARM.PuP.Web
{
    public class FilterConfig
    {
        public static void RegisterGlobalFilters(GlobalFilterCollection filters)
        {
            filters.Add(new HandleErrorAttribute());

            ModelBinders.Binders.Add(typeof(string), new TrimModelBinder());
        }
    }
}
