package com.zhq.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;

import com.zhq.api.search.ISearchService;
import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.Product;
import com.zhq.mapper.ProductMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@DubboService
public class SearchService implements ISearchService {

    @Autowired(required = false)
    private ProductMapper productMapper;

    @Autowired
    private ElasticsearchClient client;

    //同步所有数据库数据
    @Override
    public ResultBean ininAllData() {

        try {
            //判断索引是否存在
            BooleanResponse booleanResponse = client.indices().exists(e -> e.index("product"));
            if (!booleanResponse.value()) {
                //不存在则创建索引
                client.indices().create(c -> c.index("product"));
            }

            //获取数据库中的数据
            List<Product> list = productMapper.selectAll();
            for (Product product : list) {
                //添加文档数据
                client.index(i -> i
                        .index("product")
                        .id(product.getId() + "")
                        .document(product));
            }

            return ResultBean.success("1");
        } catch (IOException e) {
            e.printStackTrace();
            return ResultBean.error("网络错误！");
        }
    }

    //根据id更新数据
    @Override
    public ResultBean updateById(Long id) {
        try {
            //判断索引是否存在
            BooleanResponse booleanResponse = client.indices().exists(e -> e.index("product"));
            if (!booleanResponse.value()) {
                //不存在则创建索引
                client.indices().create(c -> c.index("product"));
            }

            //获取数据库中的数据
            Product product = productMapper.selectByPrimaryKey(id);
            client.index(i -> i
                    .index("product")
                    .id(product.getId() + "")
                    .document(product));

            return ResultBean.success("1");
        } catch (IOException e) {
            e.printStackTrace();
            return ResultBean.error("网络错误！");
        }
    }



    //查询显示数据
    //TODO 有一个错误，当查询的文档不存在时会报错
    @Override
    public List<Product> searchKey(String key, int page, int size) {

        ArrayList<Product> list = new ArrayList<>();

        //去除空格判断字符串是否为空
        if (!StringUtils.isAllEmpty(key)) {
            SearchResponse<Product> search = null;
            try {
                search = client.search(s -> s
                        .index("product")
                        //查询name字段包含
                        .query(q -> q
                                .fuzzy(f->f
                                        .field("name")
                                        .value(v->v.stringValue(key))
                                        .fuzziness("2")
                                ))
                                .from(page)
                                .size(size)
                        , Product.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Hit<Product> hit : search.hits().hits()) {
                list.add(hit.source());
            }
        }
        return list;
    }

    @Override
    public ResultBean deleteById(Long id) {
        try {
            //判断文档是否存在
            BooleanResponse exists = client.exists(g -> g
                    .index("product")
                    .id(id + ""));
            if (exists.value()) {
                DeleteResponse product = client.delete(d -> d
                        .index("product")
                        .id(id + ""));
            }

            return ResultBean.success("1");
        } catch (IOException e) {
            e.printStackTrace();
            return ResultBean.error("网络错误！");
        }
    }
}
