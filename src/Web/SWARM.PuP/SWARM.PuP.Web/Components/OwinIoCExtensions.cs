using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Microsoft.Owin;

namespace Owin
{
    public static class OwinIoCExtensions
    {
        public static T Solve<T>(this IOwinContext context)
        {
            return (T)context.Environment.GetRequestContainer().GetService(typeof(T));
        }
    }
}