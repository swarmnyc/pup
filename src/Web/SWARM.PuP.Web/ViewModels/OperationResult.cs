using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace SWARM.PuP.Web.ViewModels
{
    public class OperationResult
    {
        public bool Result { get; set; }
        public string ErrorMessage { get; set; }
    }

    public class OperationResult<T> : OperationResult
    {
        public T Value { get; set; }
    }
}