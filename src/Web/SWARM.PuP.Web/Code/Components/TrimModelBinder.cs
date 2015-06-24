using System.Web.Http.Controllers;

namespace System.Web.Mvc
{
    public class TrimModelBinder : IModelBinder
    {
        public object BindModel(ControllerContext controllerContext,
            ModelBindingContext bindingContext)
        {
            var valueResult = bindingContext.ValueProvider.GetValue(bindingContext.ModelName);
            if (valueResult == null || string.IsNullOrEmpty(valueResult.AttemptedValue))
                return null;
            return valueResult.AttemptedValue.Trim();
        }
    }
}

namespace System.Web.Http.ModelBinding
{
    public class TrimModelBinder : IModelBinder
    {
        public bool BindModel(HttpActionContext actionContext, ModelBindingContext bindingContext)
        {
            var result = bindingContext.ValueProvider.GetValue(bindingContext.ModelName);
            if (result != null)
            {
                bindingContext.Model = result.AttemptedValue.Trim();
                
                return true;
            }
            return false;
        }
    }
}