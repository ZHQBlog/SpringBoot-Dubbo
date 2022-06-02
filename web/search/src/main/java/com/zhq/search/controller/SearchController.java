package com.zhq.search.controller;

import com.zhq.api.product.IProductTypeService;
import com.zhq.api.search.ISearchService;
import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.Product;
import com.zhq.entity.ProductType;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("search")
public class SearchController {

    @DubboReference
    private ISearchService searchService;

    @DubboReference
    private IProductTypeService productTypeService;

    @RequestMapping("initAllData")
    @ResponseBody
    public ResultBean initAllData(){
        return searchService.ininAllData();
    }

    @RequestMapping("searchKey")
    public String searchKey(String key, @RequestParam(value="page", required=false) Integer page, Model model){

        if(page == null){
            page = 0;
        }
        List<Product> list = searchService.searchKey(key, page, 3);
        List<ProductType> listProductType = productTypeService.selectAll();
        model.addAttribute("listProductType",listProductType);

        model.addAttribute("list", list);
        model.addAttribute("page", page);
        model.addAttribute("key", key);

        return "search";
    }

    @RequestMapping("show")
    public String show(Model model){



        return "index";
    }
}
