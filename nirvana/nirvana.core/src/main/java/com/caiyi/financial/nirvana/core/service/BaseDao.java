package com.caiyi.financial.nirvana.core.service;

import com.caiyi.financial.nirvana.core.bean.BaseBean;

import java.util.List;

/**
 * Created by wenshiliang on 2016/4/22.
 * 数据库dao抽象接口
 */
public interface BaseDao<T extends BaseBean> {
    /**
     * 获取单条数据
     * @param id
     * @return
     */
     T get(String id);

    /**
     * 获取单条数据
     * @param entity
     * @return
     */
     T get(T entity);

    /**
     * 查询数据列表
     * @param entity
     * @return
     */
     List<T> findList(T entity);

    /**
     * 查询所有数据列表
     * @param entity
     * @return
     */
     List<T> findAllList(T entity);

    /**
     * 插入数据
     * @param entity
     * @return
     */
    public int insert(T entity);

    /**
     * 更新数据
     * @param entity
     * @return
     */
    public int update(T entity);


    /**
     * 真实删除
     * @param entity
     * @return
     */
     int realDelete(T entity);
}
