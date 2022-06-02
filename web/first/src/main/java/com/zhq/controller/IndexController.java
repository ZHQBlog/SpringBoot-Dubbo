package com.zhq.controller;

import com.zhq.api.product.IProductTypeService;
import com.zhq.entity.ProductType;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("index")
public class IndexController {

    @DubboReference
    private IProductTypeService productTypeService;

    @RequestMapping("show")
    public String show(Model model){

        List<ProductType> list = productTypeService.selectAll();
        model.addAttribute("list",list);

        return "index";
    }
}
