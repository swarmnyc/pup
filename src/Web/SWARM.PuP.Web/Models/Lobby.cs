using System;
using System.Collections.Generic;
using MongoDB;

namespace SWARM.PuP.Web.Models
{
    public class Lobby : Game
    {
        private HashSet<string> _userIds;

        public string GameId { get; set; }

        public string Description { get; set; }

        public PlayStyle PlayStyle { get; set; }

        public SkillLevel SkillLevel { get; set; }

        public string ChatRoomId { get; set; }

        public DateTime StartTimeUtc { get; set; }

        public HashSet<string> UserIds
        {
            get { return _userIds ?? (_userIds = new HashSet<string>()); }
            set { _userIds = value; }
        }
    }
}