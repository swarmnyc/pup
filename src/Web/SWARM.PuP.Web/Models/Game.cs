using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB;

namespace SWARM.PuP.Web.Models
{
    public class Game : PuPTaggable
    {
        private IList<GamePlatform> _platforms;

        public string Name { get; set; }

        public string PictureUrl { get; set; }

        public IList<GamePlatform> Platforms
        {
            get { return _platforms ?? (_platforms = new List<GamePlatform>()); }
            set { _platforms = value; }
        }
    }
}