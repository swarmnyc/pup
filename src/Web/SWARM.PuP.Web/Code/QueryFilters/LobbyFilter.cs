using System;
using System.Collections.Generic;
using System.ComponentModel;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.QueryFilters
{
    public class LobbyFilter : BaseFilter
    {
        public LobbyFilter()
        {
            Order = "StartTime";
            OrderDirection = ListSortDirection.Ascending;
        }

        public string GameId { get; set; }
        public IList<GamePlatform> Platforms { get; set; }
        public IList<PlayStyle> PlayStyles { get; set; }
        public IList<SkillLevel> SkillLevels { get; set; }
        public DateTime? StartTimeUtc { get; set; }
        public string UserId { get; set; }
    }
}