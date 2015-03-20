using System;
using System.ComponentModel;
using System.Linq;
using System.Linq.Expressions;

namespace SWARM.PuP.Web.QueryFilters
{
    public abstract class BaseFilter<T>
    {
        protected BaseFilter()
        {
            PageSize = 20;
            PageIndex = 0;
        }

        public string Search { get; set; }

        public int PageIndex { get; set; }

        public int PageSize { get; set; }

        public string Order { get; set; }

        public ListSortDirection OrderDirection { get; set; }

        protected abstract Expression<Func<T, object>> GetOrderExpression(); 

        internal virtual IQueryable<T> DoOrderQuery(IQueryable<T> query)
        {
            if (PageIndex != 0)
            {
                query = query.Skip(PageIndex * PageSize);
            }

            if (OrderDirection == ListSortDirection.Ascending)
            {
                query = query.OrderBy(GetOrderExpression());
            }
            else
            {
                query = query.OrderByDescending(GetOrderExpression());
            }

            return query.Take(PageSize);
        }
    }
}