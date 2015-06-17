using System;
using System.IO;
using System.Security.Cryptography;
using System.Text;

namespace SWARM.PuP.Web.Security
{
    public static class DataProtector
    {
        private static readonly byte[] SaltBytes = Encoding.UTF8.GetBytes("SWARMNYC");
        private static readonly byte[] PosswordBytes = Encoding.UTF8.GetBytes("SWARMNYC");

        private static readonly SHA256 HashProvider = SHA256.Create();

        public static string Protect(string text)
        {
            byte[] bytes = Encoding.ASCII.GetBytes(text);

            using (MemoryStream ms = new MemoryStream())
            {
                using (RijndaelManaged aes = new RijndaelManaged())
                {
                    aes.KeySize = 256;
                    aes.BlockSize = 128;

                    var key = new Rfc2898DeriveBytes(PosswordBytes, SaltBytes, 1000);
                    aes.Key = key.GetBytes(aes.KeySize / 8);
                    aes.IV = key.GetBytes(aes.BlockSize / 8);

                    aes.Mode = CipherMode.CBC;

                    using (var cs = new CryptoStream(ms, aes.CreateEncryptor(), CryptoStreamMode.Write))
                    {
                        cs.Write(bytes, 0, bytes.Length);
                        cs.Close();
                    }

                    bytes = ms.ToArray();
                }
            }

            return Convert.ToBase64String(bytes).Replace('+', '-').Replace('/', '*');
        }

        public static string Unprotect(string encrytedText)
        {
            encrytedText = encrytedText.Replace('-', '+').Replace('*', '/');
            byte[] bytes = Convert.FromBase64String(encrytedText);

            using (MemoryStream ms = new MemoryStream())
            {
                using (RijndaelManaged aes = new RijndaelManaged())
                {
                    aes.KeySize = 256;
                    aes.BlockSize = 128;

                    var key = new Rfc2898DeriveBytes(PosswordBytes, SaltBytes, 1000);
                    aes.Key = key.GetBytes(aes.KeySize / 8);
                    aes.IV = key.GetBytes(aes.BlockSize / 8);

                    aes.Mode = CipherMode.CBC;

                    using (var cs = new CryptoStream(ms, aes.CreateDecryptor(), CryptoStreamMode.Write))
                    {
                        cs.Write(bytes, 0, bytes.Length);
                        cs.Close();
                    }

                    bytes = ms.ToArray();
                }
            }

            return Encoding.UTF8.GetString(bytes);
        }

        public static string Hash(string password)
        {
           return Convert.ToBase64String(HashProvider.ComputeHash(Encoding.UTF8.GetBytes(password)));
        }
    }
}