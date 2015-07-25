using System.Diagnostics;
using System.Net;
using System.Net.Http;
using SWARM.PuP.Web.Services.Quickblox;

namespace System.Web.Http.Filters
{
    public class ExceptionLoggerAttribute : ExceptionFilterAttribute
    {
        public override void OnException(HttpActionExecutedContext context)
        {
            Trace.TraceWarning("Unhandled exception processing {0} for {1}\r\n{2}",
                context.Request.Method,
                context.Request.RequestUri,
                context.Exception);

            var exception = (context.Exception as HttpResponseException);
            if (exception == null)
            {
                context.Response = context.Request.CreateErrorResponse(HttpStatusCode.BadRequest,
                    "000 A error happened: " + context.Exception.Message);
            }
            else
            {
                context.Response = context.Request.CreateErrorResponse(HttpStatusCode.BadRequest, "000 A error happened: " + exception.Response.ReasonPhrase);
            }

            if (context.Exception is WebException) {
                //TODO: Only for Quickblox
                QuickbloxHttpHelper.ClearSession();
            }
        }
    }
}