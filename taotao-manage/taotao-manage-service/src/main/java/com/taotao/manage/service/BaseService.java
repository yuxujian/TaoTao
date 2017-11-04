package com.taotao.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.BasePojo;

public abstract class BaseService<T extends BasePojo> {
	
	//@Autowired
	//private Mapper<T> mapper;
	
	public abstract Mapper<T> getMapper();
	
	
	/**
	 * 根据id查询数据 
	 * @param id
	 * @return
	 */
	public T queryById(Long id) {
		return this.getMapper().selectByPrimaryKey(id);
	}
	
	/**
	 * 查询所有数据 
	 * @return
	 */
	public List<T> queryAll() {
		return this.getMapper().select(null);
	}
	
	/**
	 * 根据条件查询一条数据，如果该条件所查询的数据为多条会抛出异常
	 * @param record
	 * @return
	 */
	public T queryOne(T record) {
		return this.getMapper().selectOne(record);
	}
	
	/**
	 * 根据条件查询多条数据
	 * @param record
	 * @return
	 */
	public List<T> queryListByWhere(T record) {
		return this.getMapper().select(record);
	}
	
	/**
	 * 根据条件分页查询数据
	 * @param record
	 * @param page ： 第几次
	 * @param rows ： 该页一共有多少行
	 * @return
	 */
	public PageInfo<T> queryPageListByWhere(T record, Integer page, Integer rows) {
		//设置分页参数 
		PageHelper.startPage(page,rows);
		List<T> list = this.getMapper().select(record);
		return new PageInfo<T>(list);
	}
	
	/**
	 * 新增数据 
	 * @param t
	 * @return
	 */
	public Integer save(T t) {
		t.setCreated(new Date());
		t.setUpdated(t.getCreated());
		return this.getMapper().insert(t);
	}
	
	/**
	 * 选择不为null 的字段作为插入数据的字段 来插入数据 
	 * @param t
	 * @return
	 */
	public Integer saveSelective(T t) {
		t.setCreated(new Date());
		t.setUpdated(t.getCreated());
		return this.getMapper().insertSelective(t);
	}
	
	
	/**
	 * 更新数据 
	 * @param t
	 * @return
	 */
	public Integer update(T t) {
		t.setUpdated(new Date());
		return this.getMapper().updateByPrimaryKey(t);
	}
	
	/**
	 * 选择不为null 的字段作为更新数据的字段 来更新数据 
	 * @param t
	 * @return
	 */
	public Integer updateSelective(T t) {
		t.setUpdated(new Date());
		t.setCreated(null); //强制设置创建时间为null , 永远不会被更新
		return this.getMapper().updateByPrimaryKeySelective(t);
	}
	
	/**
	 * 根据主键id删除数据 （物理删除）
	 * @param id
	 * @return
	 */
	public Integer deleteById(Long id) {
		return this.getMapper().deleteByPrimaryKey(id);
	}
	
	/**
	 * 批量删除数据 
	 * @param ids
	 * @param clazz
	 * @param property
	 * @return
	 */
	public Integer deleteByIds(List<Object> ids, Class<T> clazz, String property) {
		Example example = new Example(clazz);
		//设置条件
		example.createCriteria().andIn(property, ids);
		return this.getMapper().deleteByExample(example);
	}
	
	/**
	 * 根据条件删除数据 
	 * @param record
	 * @return
	 */
	public Integer deleteByWhere(T record) {
		return this.getMapper().delete(record);
	}
}
