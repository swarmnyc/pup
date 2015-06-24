using MongoDB.Bson;

namespace SWARM.PuP.Web.Models
{
    public class PuPTag
    {
        private string _id;

        public PuPTag()
        {
        }

        public PuPTag(string key, string value)
        {
            this.Key = key;
            this.Value = value;
        }

        public string Id
        {
            get { return _id ?? (_id = ObjectId.GenerateNewId().ToString()); }
            set { _id = value; }
        }

        public string Key { get; set; }

        public string Value { get; set; }
    }
}