package com.zhq.backgroud.controller;

import com.zhq.api.product.IProductTypeService;
import com.zhq.entity.ProductType;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("productType")
public class ProductTypeController {

    @DubboReference
    private IProductTypeService productTypeService;

    @GetMapping("list")
    public List<ProductType> list(){
        return productTypeService.selectAll();
    }
}
