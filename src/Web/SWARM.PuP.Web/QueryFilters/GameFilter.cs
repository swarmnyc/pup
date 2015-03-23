using System.Collections.Generic;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.QueryFilters
{
    public class GameFilter : BaseFilter
    {
        public IList<GamePlatform> Platforms { get; set; }
    }
}