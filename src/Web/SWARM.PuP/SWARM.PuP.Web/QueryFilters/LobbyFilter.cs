using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq.Expressions;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.QueryFilters
{
    public class LobbyFilter : BaseFilter<Lobby>
    {
        public LobbyFilter()
        {
            this.Order = "StartTime";
            this.OrderDirection = ListSortDirection.Descending;
        }

        public IList<GamePlatform> Platforms { get; set; }
        public IList<PlayStyle> PlayStyles { get; set; }

        public IList<SkillLevel> SkillLevels { get; set; }

        protected override Expression<Func<Lobby, object>> GetOrderExpression()
        {
            if (string.IsNullOrWhiteSpace(this.Order))
            {
                return (Lobby x) => x.StartTimeUtc;
            }
            else
            {
                switch (Order.ToLower())
                {
                    case "name":
                        return (Lobby x) => x.Name;
                    case "popular":
                        return (Lobby x) => x.UserIds.Count;
                    case "starttime":
                    default:
                        return (Lobby x) => x.StartTimeUtc;
                }
            }
        }
    }
}