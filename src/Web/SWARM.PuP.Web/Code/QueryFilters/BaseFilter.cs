using System.ComponentModel;

namespace SWARM.PuP.Web.QueryFilters
{
    public abstract class BaseFilter
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
    }
}