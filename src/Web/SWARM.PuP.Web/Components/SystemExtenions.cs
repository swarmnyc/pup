using System.Collections;
using System.Security.Principal;
using Microsoft.AspNet.Identity;
using SWARM.PuP.Web;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Services;

namespace System
{
    public static class SystemExtenions 
    {
        public static Boolean IsNullOrEmpty(this IEnumerable enumerable)
        {
            if (enumerable == null)
                return true;

            if (!enumerable.GetEnumerator().MoveNext())
                return true;

            return false;
        }

        public static Boolean IsNullOrEmpty(this ICollection enumerable)
        {
            if (enumerable == null)
                return true;

            if (enumerable.Count==0)
                return true;

            return false;
        }

        public static Boolean IsNullOrEmpty(this Array enumerable)
        {
            if (enumerable == null)
                return true;

            if (enumerable.Length == 0)
                return true;

            return false;
        }

        public static PuPUser Get(this IUserService service, IIdentity identity)
        {
            return service.GetById(identity.GetUserId());
        }
    }
}