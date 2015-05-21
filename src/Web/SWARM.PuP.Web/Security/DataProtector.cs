using System;
using System.Security.Cryptography;
using System.Text;

namespace SWARM.PuP.Web.Security
{
    public static class DataProtector
    {
        private static SHA256 hashProvider = SHA256Managed.Create();
        private static readonly DpapiDataProtector Protector = new DpapiDataProtector("PuP", "PuP.Swarmnyc");

        public static string Protect(string text)
        {
            byte[] bytes = Protector.Protect(Encoding.ASCII.GetBytes(text));
            return Convert.ToBase64String(bytes).Replace('+', '-').Replace('/', '*');
        }

        public static string Unprotect(string encrytedText)
        {
            encrytedText = encrytedText.Replace('-', '+').Replace('*', '/');
            byte[] bytes = Protector.Unprotect(Convert.FromBase64String(encrytedText));
            return Encoding.ASCII.GetString(bytes);
        }

        public static string Hash(string password)
        {
           return Convert.ToBase64String(hashProvider.ComputeHash(Encoding.ASCII.GetBytes(password)));
        }
    }
}