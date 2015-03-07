using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Mango;

namespace SWARM.PuP.Web.Models
{
    public class Game : MangoModel
    {
        private List<GamePlatfrom> _platforms;
        private List<string> _tags;

        public string Name { get; set; }

        public string ChatRoomId { get; set; }

        public List<GamePlatfrom> Platforms
        {
            get { return _platforms ?? new List<GamePlatfrom>(); }
            set { _platforms = value; }
        }

        public List<string> Tags
        {
            get { return _tags ?? new List<string>(); }
            set { _tags = value; }
        }
    }
}