package com.zhq.api;

import com.zhq.common.pojo.ResultBean;

import java.util.List;

/**
 * 生成详情页接口
 */
public interface IitemService {

    //批量更新详情页面
    ResultBean batchCreateHTML(List<Long> list);

    //生成商品的详情静态页面
    ResultBean createHTMLById(Long id);
}
