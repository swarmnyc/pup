using System;
using System.Collections.Generic;
using MongoDB;

namespace SWARM.PuP.Web.Models
{
    public class Lobby : Game
    {
        private HashSet<LobbyUserInfo> _users;

        public string GameId { get; set; }

        public string Description { get; set; }
        
        public PlayStyle PlayStyle { get; set; }

        public SkillLevel SkillLevel { get; set; }

        public DateTime StartTimeUtc { get; set; }

        public HashSet<LobbyUserInfo> Users
        {
            get { return _users ?? (_users = new HashSet<LobbyUserInfo>()); }
            set { _users = value; }
        }
    }
}