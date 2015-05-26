using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;

namespace SWARM.PuP.Web.Services
{
    public interface IImageService
    {
        Stream CreateThumbnail(Stream input);
        void CreateThumbnailTo(Stream input, string saveto);
    }
}