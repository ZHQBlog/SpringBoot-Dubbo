package com.zhq.common.base;

import java.util.List;

/**
 * 整合serviceImpl的公共方法，不用重复写公共方法
 * 不知道实现的是哪个类的方法，所以mapper也不确定，使用抽象方法指定mapper的类型
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    public abstract IBaseDao<T> getBaseDao();

    @Override
    public int deleteByPrimaryKey(Long id) {
        return getBaseDao().deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByPrimaryKeys(Long[] ids) {
        return getBaseDao().deleteByPrimaryKeys(ids);
    }

    @Override
    public int insert(T t) {
        return getBaseDao().insert(t);
    }

    @Override
    public int insertSelective(T t) {
        return getBaseDao().insertSelective(t);
    }

    @Override
    public T selectByPrimaryKey(Long id) {
        return getBaseDao().selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(T t) {
        return getBaseDao().updateByPrimaryKeySelective(t);
    }

    @Override
    public int updateByPrimaryKey(T t) {
        return getBaseDao().updateByPrimaryKey(t);
    }

    @Override
    public List<T> selectAll() {
        return getBaseDao().selectAll();
    }
}
