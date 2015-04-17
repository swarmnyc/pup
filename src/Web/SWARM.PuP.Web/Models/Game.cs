using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace SWARM.PuP.Web.Models
{
    public class Game : PuPTaggable
    {
        private IList<GamePlatform> _platforms;

        public string Name { get; set; }

        public string ThumbnailPictureUrl { get; set; }

        public string PictureUrl { get; set; }

        public string Description { get; set; }

        public DateTime? ReleaseDate { get; set; }

        public string[] GameTypes { get; set; }

        [BsonRepresentation(BsonType.String)]
        public IList<GamePlatform> Platforms
        {
            get { return _platforms ?? (_platforms = new List<GamePlatform>()); }
            set { _platforms = value; }
        }
    }
}