package com.zhq.api.search;

import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.Product;

import java.util.List;

/**
 * 查询接口
 */
public interface ISearchService {
    //全量同步数据
    ResultBean ininAllData();

    //增量同步数据
    ResultBean updateById(Long id);

    //显示数据
    List<Product> searchKey(String key, int page, int size);

    //删除同步数据
    ResultBean deleteById(Long id);
}
