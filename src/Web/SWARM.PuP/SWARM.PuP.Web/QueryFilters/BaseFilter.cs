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

        internal virtual IQueryable<T> DoOrderQuery(IQueryable<T> collection)
        {
            if (PageIndex != 0)
            {
                collection = collection.Skip(PageIndex * PageSize);
            }

            if (OrderDirection == ListSortDirection.Ascending)
            {
                collection = collection.OrderBy(GetOrderExpression());
            }
            else
            {
                collection = collection.OrderByDescending(GetOrderExpression());
            }

            return collection.Take(PageSize);
        }
    }
}