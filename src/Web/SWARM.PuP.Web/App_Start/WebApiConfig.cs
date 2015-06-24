using System;
using System.Linq;
using System.Web.Http;
using System.Web.Http.Filters;
using System.Web.Http.ModelBinding;
using MultipartDataMediaFormatter;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Serialization;

namespace SWARM.PuP.Web
{
    public static class WebApiConfig
    {
        private class ReadonlyFormMultipartEncodedMediaTypeFormatter : FormMultipartEncodedMediaTypeFormatter
        {
            public override bool CanWriteType(Type type)
            {
                return false;
            }
        }

        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services
            config.Filters.Add(new ExceptionLoggerAttribute());

            // Use camel case for JSON data.
            config.Formatters.JsonFormatter.SerializerSettings.ContractResolver =
                new CamelCasePropertyNamesContractResolver();
            config.Formatters.JsonFormatter.SerializerSettings.NullValueHandling = NullValueHandling.Ignore;
            config.Formatters.JsonFormatter.SerializerSettings.DateFormatString = "yyyy-MM-ddTHH:mm:ssZ";
            config.Formatters.JsonFormatter.SerializerSettings.Converters.Add(new StringEnumConverter());

            Json.Settings = config.Formatters.JsonFormatter.SerializerSettings;

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute("DefaultApi", "api/{controller}/{id}", new {id = RouteParameter.Optional});
            
            config.Formatters.Add(new ReadonlyFormMultipartEncodedMediaTypeFormatter());
            
            config.Formatters.Remove(config.Formatters.XmlFormatter);

            config.BindParameter(typeof(string), new TrimModelBinder());

        }
    }
}