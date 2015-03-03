using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web.Helpers;

namespace System.Net
{
    public static class WebHelper
    {
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
                writer.Write(Json.Encode(obj));
            }
        }

        public static T Read<T>(this WebResponse response)
        {
            using (var writer = new StreamReader(response.GetResponseStream()))
            {
                return Json.Decode<T>(writer.ReadToEnd());
            }
        }

        public static string ReadAll(this WebResponse response)
        {
            using (var writer = new StreamReader(response.GetResponseStream()))
            {
                return writer.ReadToEnd();
            }
        }
    }
}