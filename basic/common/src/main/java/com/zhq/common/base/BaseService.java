package com.zhq.common.base;

import java.util.List;

/**
 * 整合service接口公共方法的接口，不用重复写公共方法
 */
public interface BaseService<T> {
    int deleteByPrimaryKey(Long id);
    int deleteByPrimaryKeys(Long[] ids);
    int insert(T t);
    int insertSelective(T t);
    T selectByPrimaryKey(Long id);
    int updateByPrimaryKeySelective(T t);
    int updateByPrimaryKey(T t);
    List<T> selectAll();
}
