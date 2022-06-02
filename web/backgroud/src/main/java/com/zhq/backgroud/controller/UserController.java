package com.zhq.backgroud.controller;


import com.github.pagehelper.PageInfo;
import com.zhq.api.IUserService;
import com.zhq.entity.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("user")
public class UserController {

    @DubboReference
    private IUserService userService;

    @GetMapping("page/{pageIndex}/{pageSize}")
    public String page(Model model, @PathVariable("pageIndex")Integer pageIndex,
                       @PathVariable("pageSize")Integer pageSize){

        //调用分页的service，得到分页数据
        PageInfo<User> pageInfo = userService.page(pageIndex, pageSize);
        model.addAttribute("pageInfo", pageInfo);

        return "user/list";
    }

    @PostMapping("deleteAll")
    public String deleteAll(Long[] userId){

        //批量删除
        if (userId != null) {
            userService.deleteAll(userId);
        }

        return "redirect:/user/page/1/3";
    }

    @PostMapping("delete")
    public String delete(Long pId){

        //删除单个信息
        if (pId != null) {
            userService.delete(pId);
        }

        return "redirect:/user/page/1/3";
    }
}
