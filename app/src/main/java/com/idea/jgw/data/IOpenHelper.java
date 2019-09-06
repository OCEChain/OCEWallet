package com.idea.jgw.data;

import java.util.Collection;
import java.util.List;

public interface IOpenHelper {
    void save(Object obj); //保存数据
    void saveAll(Collection collection); //保存所有数据
    public void saveAll(String tableName,Collection collection);
    <T> List<T> queryAll(Class<T> table); //根据类名（表名）查找所有的数据
    public <T> List<T> queryAll(String tableName,Class<T> table);
    public <T> List<T> queryAll(String tableName,Class<T> table, String orderBy);
    <T> List<T> queryAll(Class<T> table, String order); //通过排序的方式查找所有的数据
    <T> List<T> queryAll(Class<T> table, String order, int limit); //通过排序和一夜显示多少条数据来查询所有的数据
    <T> T queryById(Class<T> table, Object id);//通过id查找对应的数据
    void clear(Class table); //清空对应表名中的所有数据
    void clear(String table); //清空对应表名中的所有数据
    void delete(Object obj); //删除对应的数据;
    void deleteAll(Collection collection); // 删除集合中所有的数据

}