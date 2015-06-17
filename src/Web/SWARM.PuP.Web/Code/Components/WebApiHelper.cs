using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web.Helpers;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace System.Net
{
    public static class WebApiHelper
    {
        static WebApiHelper()
        {
            JsonConvert.DefaultSettings = () => new JsonSerializerSettings
            {
                Formatting = Formatting.None,
                NullValueHandling = NullValueHandling.Ignore,
                ContractResolver = new CamelCasePropertyNamesContractResolver()
            };
        }

        public static void Write(this WebRequest request, string content)
        {
            using (var writer = new StreamWriter(request.GetRequestStream()))
            {
                writer.Write(content);
            }
        }

        public static void Write(this WebRequest request, object obj)
        {
            using (var writer = new StreamWriter(request.GetRequestStream()))
            {
                
                writer.Write(JsonConvert.SerializeObject(obj));
            }
        }

        public static T Read<T>(this WebResponse response)
        {
            using (var writer = new StreamReader(response.GetResponseStream()))
            {
                return JsonConvert.DeserializeObject<T>(writer.ReadToEnd());
            }
        }

        public static string ReadAll(this WebResponse response)
        {
            using (var writer = new StreamReader(response.GetResponseStream()))
            {
                return writer.ReadToEnd();
            }
        }

        public static T Json<T>(this WebRequest request, object obj)
        {
            request.Write(obj);
            return request.GetResponse().Read<T>();
        }

        public static T Json<T>(this WebRequest request)
        {
            return request.GetResponse().Read<T>();
        }
    }
}