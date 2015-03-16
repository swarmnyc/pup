using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using SWARM.PuP.Web.Models;

namespace SWARM.PuP.Web.QueryFilters
{
    public class GameFilter : BaseFilter<Game>
    {
        public GameFilter()
        {
        }

        public IList<GamePlatform> Platforms { get; set; }

        protected override Expression<Func<Game, object>> GetOrderExpression()
        {
            if (string.IsNullOrWhiteSpace(this.Order))
            {
                return (Game x) => x.Name;
            }
            else
            {
                switch (Order.ToLower())
                {
                    case "name":
                    default:
                        return (Game x) => x.Name;
                }
            }
        }
    }
}