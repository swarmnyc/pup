using System.Collections;

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
    }
}