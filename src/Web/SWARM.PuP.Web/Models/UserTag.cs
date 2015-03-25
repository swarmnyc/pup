using MongoDB.Bson;

namespace SWARM.PuP.Web.Models
{
    public class UserTag
    {
        private string _id;

        public UserTag(UserTagType type, string key, string value)
        {
            this.Type = type;
            this.Key = key;
            this.Value = value;
        }

        public string Id
        {
            get { return _id ?? (_id = ObjectId.GenerateNewId().ToString()); }
            set { _id = value; }
        }

        public UserTagType Type { get; set; }

        public string Key { get; set; }

        public string Value { get; set; }
    }

    public enum UserTagType : int
    {
        Application = 0,
        Platform = 1,
        Game =2
    }
}