using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;

namespace System
{
    public static class Json
    {
        public static JsonSerializerSettings Settings { get; set; }


        public static string ToJson(this object obj)
        {
            return JsonConvert.SerializeObject(obj, Json.Settings);
        }

        public static T ToObject<T>(this string json)
        {
            return JsonConvert.DeserializeObject<T>(json, Json.Settings);
        }
    }
}