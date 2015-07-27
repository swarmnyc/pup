using System;
using System.Collections.Generic;
using MongoDB;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace SWARM.PuP.Web.Models
{
    public class Lobby : PuPTaggable
    {
        private HashSet<LobbyUserInfo> _users;

        public string GameId { get; set; }

        public string Name { get; set; }

        public string PictureUrl { get; set; }

        public string ThumbnailPictureUrl { get; set; }

        public string Description { get; set; }

        [BsonRepresentation(BsonType.String)]
        public GamePlatform Platform { get; set; }

        [BsonRepresentation(BsonType.String)]
        public PlayStyle PlayStyle { get; set; }

        [BsonRepresentation(BsonType.String)]
        public SkillLevel SkillLevel { get; set; }

        public DateTime StartTimeUtc { get; set; }

        public HashSet<LobbyUserInfo> Users
        {
            get { return _users ?? (_users = new HashSet<LobbyUserInfo>()); }
            set { _users = value; }
        }

        [BsonIgnore]
        public string LastMessage { get; set; }
        [BsonIgnore]
        public DateTime LastMessageAt { get; set; }
        [BsonIgnore]
        public int UnreadMessageCount { get; set; }

        public override string ToString()
        {
            return Name + "("+ Id +")";
        }
    }
}