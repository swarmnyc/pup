using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB;

namespace SWARM.PuP.Web.Models
{
    public class Game : MongoModel
    {
        private IList<GamePlatform> _platforms;
        private IList<string> _tags;

        public string Name { get; set; }

        public string PictureUrl { get; set; }

        public IList<GamePlatform> Platforms
        {
            get { return _platforms ?? new List<GamePlatform>(); }
            set { _platforms = value; }
        }

        public IList<string> Tags
        {
            get { return _tags ?? new List<string>(); }
            set { _tags = value; }
        }
    }
}