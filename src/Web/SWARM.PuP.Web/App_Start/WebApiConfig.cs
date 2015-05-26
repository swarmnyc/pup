using System;
using System.Linq;
using System.Web.Http;
using System.Web.Http.Filters;
using MultipartDataMediaFormatter;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Serialization;

namespace SWARM.PuP.Web
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services
            config.Filters.Add(new ExceptionLoggerAttribute());
            config.Filters.Add(new ValidateViewModelAttribute());


            // Use camel case for JSON data.
            config.Formatters.JsonFormatter.SerializerSettings.ContractResolver =
                new CamelCasePropertyNamesContractResolver();
            config.Formatters.JsonFormatter.SerializerSettings.NullValueHandling = NullValueHandling.Ignore;
            config.Formatters.JsonFormatter.SerializerSettings.DateFormatString = "yyyy-MM-ddTHH:mm:ssZ";
            config.Formatters.JsonFormatter.SerializerSettings.Converters.Add(new StringEnumConverter());

            Json.Settings = config.Formatters.JsonFormatter.SerializerSettings;

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute("DefaultApi", "api/{controller}/{id}", new {id = RouteParameter.Optional}
                );
            
            config.Formatters.Add(new FormMultipartEncodedMediaTypeFormatter());
            config.Formatters.Remove(config.Formatters.XmlFormatter);
        }
    }
}