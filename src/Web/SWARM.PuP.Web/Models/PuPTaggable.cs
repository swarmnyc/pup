using System.Collections.Generic;
using System.Linq;
using MongoDB;

namespace SWARM.PuP.Web.Models
{
    public interface IPuPTaggable
    {
        List<PuPTag> Tags { get; set; }
    }

    public abstract class PuPTaggable : MongoModel, IPuPTaggable
    {
        private List<PuPTag> _tags;

        public List<PuPTag> Tags
        {
            get { return _tags ?? (_tags = new List<PuPTag>()); }
            set { _tags = value; }
        }
    }

    public static class PuPTaggableExtenstions
    {
        public static void AddTag(this IPuPTaggable taggable, string key, string value)
        {
            taggable.Tags.Add(new PuPTag(key, value));
        }

        public static void UpdateTag(this IPuPTaggable taggable, string key, string value)
        {
            PuPTag tag = taggable.Tags.FirstOrDefault(t => t.Key == key);
            if (tag == null)
            {
                tag = new PuPTag(key, value);
                taggable.Tags.Add(tag);
            }
            else
            {
                tag.Value = value;
            }
        }

        public static string GetTagValue(this IPuPTaggable taggable, string key)
        {
            var tag = taggable.Tags.FirstOrDefault(t => t.Key == key);
            return tag == null ? null : tag.Value;
        }

        public static string[] GetTagValues(this IPuPTaggable taggable, string key)
        {
            return taggable.Tags.Where(t => t.Key == key).Select(x => x.Value).ToArray();
        }
    }
}