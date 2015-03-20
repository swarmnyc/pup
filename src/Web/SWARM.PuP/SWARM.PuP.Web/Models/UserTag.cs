namespace SWARM.PuP.Web.Models
{
    public class UserTag
    {
        public UserTagType Type { get; set; }
        public string Key { get; set; }

        public string Value { get; set; }
    }

    public enum UserTagType
    {
        PuP,
        Platform,
        Game
    }
}