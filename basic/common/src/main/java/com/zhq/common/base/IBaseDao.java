package com.zhq.common.base;

import java.util.List;

/**
 * 整合mapper接口的重发方法
 */
public interface IBaseDao<T> {
    int deleteByPrimaryKey(Long id);
    int deleteByPrimaryKeys(Long[] ids);
    int insert(T t);
    int insertSelective(T t);
    T selectByPrimaryKey(Long id);
    int updateByPrimaryKeySelective(T t);
    int updateByPrimaryKey(T t);
    List<T> selectAll();
}
