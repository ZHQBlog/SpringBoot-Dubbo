package com.zhq.mapper;


import com.zhq.common.base.IBaseDao;
import com.zhq.entity.User;

public interface UserMapper  extends IBaseDao<User> {
    User selectByUsername(String name);
}