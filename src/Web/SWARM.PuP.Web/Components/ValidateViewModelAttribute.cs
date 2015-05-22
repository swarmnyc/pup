using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http.Controllers;
using SWARM.PuP.Web.ApiControllers;

namespace System.Web.Http.Filters
{
    public class ValidateViewModelAttribute : ActionFilterAttribute
    {
        public override void OnActionExecuting(HttpActionContext actionContext)
        {
            if (actionContext.ActionArguments.Any(kv => kv.Value == null))
            {
                actionContext.Response = actionContext.Request.CreateErrorResponse(HttpStatusCode.BadRequest,
                    ErrorCode.E001WrongParameter);
            }

            if (actionContext.ModelState.IsValid == false)
            {
                actionContext.Response = actionContext.Request.CreateErrorResponse(HttpStatusCode.BadRequest,
                    ErrorCode.E001WrongParameter);
            }
        }
    }
}