using System.Collections;
using System.IO;
using System.Linq;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace System.Net.Http
{
    public class JsonContent : HttpContent
    {
        private readonly object _value;

        public JsonContent(object value)
        {
            _value = value;
            Headers.ContentType = new MediaTypeHeaderValue("application/json");
        }

        protected override Task SerializeToStreamAsync(Stream stream,
            TransportContext context)
        {
            using (var writer = new StreamWriter(stream))
            {
                return writer.WriteAsync(JsonConvert.SerializeObject(_value));
            }
        }

        protected override bool TryComputeLength(out long length)
        {
            length = -1;
            return false;
        }
    }
}

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