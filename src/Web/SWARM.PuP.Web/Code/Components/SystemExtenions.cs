using System.Collections;
using System.Security.Principal;
using SWARM.PuP.Web.Models;
using SWARM.PuP.Web.Security;
using SWARM.PuP.Web.Services;

namespace System
{
    public static class SystemExtenions
    {
        public static bool IsNullOrEmpty(this IEnumerable enumerable)
        {
            if (enumerable == null)
                return true;

            var s = enumerable as String;
            if (s != null)
                return s.Length == 0;

            var array = enumerable as Array;
            if (array != null)
                return array.Length == 0;

            var collection = enumerable as ICollection;
            if (collection != null)
                return collection.Count == 0;

            return !enumerable.GetEnumerator().MoveNext();
        }

        public static bool IsNotNullOrEmpty(this IEnumerable enumerable)
        {
            if (enumerable == null)
                return false;

            var s = enumerable as String;
            if (s != null)
                return s.Length != 0;

            var array = enumerable as Array;
            if (array != null)
                return array.Length != 0;

            var collection = enumerable as ICollection;
            if (collection != null)
                return collection.Count != 0;

            return enumerable.GetEnumerator().MoveNext();
        }

        public static bool IsNullOrWhiteSpace(this string s)
        {
            return string.IsNullOrWhiteSpace(s);
        }

        public static bool IsNotNullOrWhiteSpace(this string s)
        {
            return !string.IsNullOrWhiteSpace(s);
        }

        public static PuPUser GetPuPUser(this IIdentity identity)
        {
            return ((PuPClaimsIdentity) identity).User;
        }
    }
}