using System.IO;
using System.Linq;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web.Http;
using Newtonsoft.Json;

namespace System.Net.Http
{
    public class JsonContent : HttpContent
    {
        private readonly object _value;
        public static JsonSerializerSettings Settings { get; set; }

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
                return writer.WriteAsync(JsonConvert.SerializeObject(_value, Settings));
            }
        }

        protected override bool TryComputeLength(out long length)
        {
            length = -1;
            return false;
        }
    }
}