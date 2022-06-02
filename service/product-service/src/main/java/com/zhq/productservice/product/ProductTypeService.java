package com.zhq.productservice.product;

import com.zhq.api.product.IProductTypeService;
import com.zhq.common.base.BaseServiceImpl;
import com.zhq.common.base.IBaseDao;
import com.zhq.entity.ProductType;
import com.zhq.mapper.ProductTypeMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DubboService
public class ProductTypeService extends BaseServiceImpl<ProductType> implements IProductTypeService {

    @Autowired(required = false)
    private ProductTypeMapper productTypeMapper;

    @Override
    public IBaseDao<ProductType> getBaseDao() {
        return productTypeMapper;
    }
}
