package com.taotao.manage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class RedisService{
	
	@Autowired
	private ShardedJedisPool shardedJedisPool;
	
	//其实我们<T>可以放在public class RedisService<T> {...}里，但是这样<T>的作用范围过大， 所以我们把它放在方法体里， 放在这里表示 作用范围只在execute里
	private <T> T execute(Function<T, ShardedJedis> fun) {
		ShardedJedis shardedJedis = null;
		try {
			//从连接池中获取到jedis分片对象
			shardedJedis = shardedJedisPool.getResource();
			return fun.callback(shardedJedis);
		} finally {
			if(null != shardedJedis) {
				//关闭检测连接是否有效， 有效则放回到连接池， 无效则重置状态
				shardedJedis.close();
			}
		}
	}
	
	/**
	 * 执行set操作
	 * @param key
	 * @param value
	 * @return
	 */
	public String set(final String key, final String value) {
		return this.execute(new Function<String, ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.set(key, value); 
			}
		});
	}
	
	/**
	 * 执行get操作
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		return this.execute(new Function<String, ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				return e.get(key); 
			}
		});
	}
	
	/**
	 * 执行删除操作
	 * @param key
	 * @return
	 */
	public Long del(final String key) {
		return this.execute(new Function<Long, ShardedJedis>(){
			@Override
			public Long callback(ShardedJedis e) {
				return e.del(key); 
			}
		});
	}
	
	/**
	 * 设置生存时间， 单位为：秒
	 * @param key
	 * @param seconds
	 * @return
	 */
	public Long del(final String key, final Integer seconds) {
		return this.execute(new Function<Long, ShardedJedis>(){
			@Override
			public Long callback(ShardedJedis e) {
				return e.expire(key,seconds); 
			}
		});
	}
	
	
	/**
	 * 执行set操作并且设置生存时间， 单位为：秒
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public String set(final String key, final String value,final Integer seconds) {
		return this.execute(new Function<String, ShardedJedis>(){
			@Override
			public String callback(ShardedJedis e) {
				String str = e.set(key, value);
				e.expire(key, seconds);
				return str; 
			}
		});
	}
}
