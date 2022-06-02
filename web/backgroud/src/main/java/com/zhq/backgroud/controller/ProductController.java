package com.zhq.backgroud.controller;

import com.github.pagehelper.PageInfo;
import com.zhq.api.IitemService;
import com.zhq.api.product.IProductService;
import com.zhq.api.search.ISearchService;
import com.zhq.api.vo.ProductVO;
import com.zhq.common.constant.RabbitMQConstant;
import com.zhq.entity.Product;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("product")
public class ProductController {

    @DubboReference
    private IProductService productService;
    
    @DubboReference
    private ISearchService searchService;

    @DubboReference
    private IitemService itemService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 分页
     * @param model
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("page/{pageIndex}/{pageSize}")
    public String page(Model model, @PathVariable("pageIndex")Integer pageIndex,
                       @PathVariable("pageSize")Integer pageSize){

        //调用分页的service，得到分页数据
        PageInfo<Product> pageInfo = productService.page(pageIndex, pageSize);
        model.addAttribute("pageInfo", pageInfo);

        return "product/list";
    }

    /**
     * 添加商品信息
     * @param productVO
     * @return
     */
    @PostMapping("add")
    public String add(ProductVO productVO){

        //返回的是商品id
        Long productId = productService.add(productVO);

        //发送消息到交换机，实现异步调用
        rabbitTemplate.convertAndSend(RabbitMQConstant.BACKGROUD_EXCHANGE, "product.add", productId);

        //调用搜索服务，同步数据
        //searchService.updateById(productId);

        //生成今天详情页
        //itemService.createHTMLById(productId);

        return "redirect:/product/page/1/3";
    }

    @PostMapping("deleteAll")
    public String deleteAll(Long[] productId){

        //批量删除
        if (productId != null) {
            productService.deleteAll(productId);
            for (Long id : productId) {
                //发送消息到交换机
                rabbitTemplate.convertAndSend(RabbitMQConstant.BACKGROUD_EXCHANGE, "product.delete", productId);
            }
        }

        return "redirect:/product/page/1/3";
    }

    @PostMapping("delete")
    public String delete(Long pId){

        //删除单个信息
        if (pId != null){
            productService.delete(pId);
            //发送消息到交换机
            rabbitTemplate.convertAndSend(RabbitMQConstant.BACKGROUD_EXCHANGE, "product.delete", pId);
        }

        return "redirect:/product/page/1/3";
    }

    @PostMapping("update")
    public String update(ProductVO productVO){

        productService.update(productVO);

        return "redirect:/product/page/1/3";
    }

    @PostMapping("updateAjax")
    @ResponseBody
    public Product updateAjax(Long id){

        Product product = productService.selectByPrimaryKey(id);
        return product;
    }
}
