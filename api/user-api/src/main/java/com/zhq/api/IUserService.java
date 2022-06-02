package com.zhq.api;


import com.github.pagehelper.PageInfo;
import com.zhq.common.base.BaseService;
import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.User;

/**
 * 登录接口
 */
public interface IUserService extends BaseService<User> {

    User checkLogin(User user);
    ResultBean checkIsLogin(String uuid);
    User queryUser(String name);
    PageInfo<User> page(Integer pageIndex, Integer pageSize);
    void deleteAll(Long[] userId);
    void delete(Long pId);
}
