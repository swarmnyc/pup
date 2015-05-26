using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;

namespace SWARM.PuP.Web.Services
{
    public class ImageService : IImageService
    {
        public Stream CreateThumbnail(Stream input)
        {
            Stream output;
            using (var img = Image.FromStream(input))
            {
                var widthRatio = (float)img.Width / 1024;
                var heightRatio = (float)img.Height / 1024;
                if (widthRatio > 1 && heightRatio > 1)
                {
                    // Resize to the greatest ratio
                    var ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
                    var newWidth = Convert.ToInt32(Math.Floor(img.Width / ratio));
                    var newHeight = Convert.ToInt32(Math.Floor(img.Height / ratio));

                    var thumb = img.GetThumbnailImage(newWidth, newHeight, ThumbnailImageAbortCallback, IntPtr.Zero);
                    output = new MemoryStream();
                    thumb.Save(output, ImageFormat.Png);
                    thumb.Dispose();

                    output.Position = 0;
                }
                else
                {
                    output = input;
                }
            }

            return output;
        }

        public void CreateThumbnailTo(Stream input, string saveto)
        {
            using (Stream thumbnail = CreateThumbnail(input))
            {
                byte[] bytes = new byte[4096];

                using (var writer = new FileStream(saveto, FileMode.Create))
                {
                    int size;
                    while ((size = thumbnail.Read(bytes, 0, bytes.Length)) > 0)
                    {
                        writer.Write(bytes, 0, size);
                    }
                }
            }
        }

        public static bool ThumbnailImageAbortCallback()
        {
            return true;
        }
    }
}