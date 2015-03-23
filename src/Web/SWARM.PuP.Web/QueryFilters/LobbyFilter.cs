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
            OrderDirection = ListSortDirection.Descending;
        }

        public IList<GamePlatform> Platforms { get; set; }
        public IList<PlayStyle> PlayStyles { get; set; }
        public IList<SkillLevel> SkillLevels { get; set; }
    }
}