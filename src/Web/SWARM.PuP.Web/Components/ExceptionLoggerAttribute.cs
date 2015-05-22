using System.Diagnostics;
using System.Net;
using System.Net.Http;

namespace System.Web.Http.Filters
{
    public class ExceptionLoggerAttribute : ExceptionFilterAttribute
    {
        public override void OnException(HttpActionExecutedContext context)
        {
            Trace.TraceError("Unhandled exception processing {0} for {1}: {2}",
                context.Request.Method,
                context.Request.RequestUri,
                context.Exception);

            context.Response = context.Request.CreateErrorResponse(HttpStatusCode.BadRequest, "A error happened");
        }
    }
}