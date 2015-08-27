package com.swarmnyc.pup.module.service.Filter;

import com.swarmnyc.pup.module.service.ListSortDirection;
import com.swarmnyc.pup.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseFilter {
    String search = "";
    int pageIndex = -1;
    int pageSize = -1;
    String order;
    ListSortDirection orderDirection = ListSortDirection.Descending;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public ListSortDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(ListSortDirection orderDirection) {
        this.orderDirection = orderDirection;
    }


    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(search))
            map.put("search", search);

        if (StringUtils.isNotEmpty(order)){
            map.put("order", order);
            map.put("orderDirection", orderDirection.toString());
        }

        if (pageIndex!=-1)
            map.put("pageIndex",String.valueOf(pageIndex));

        if (pageSize!=-1)
            map.put("pageSize",String.valueOf(pageSize));

        return map;
    }
}
