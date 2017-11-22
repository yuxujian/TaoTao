package com.taotao.manage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.mapper.Mapper;
import com.taotao.common.bean.ItemCatData;
import com.taotao.common.bean.ItemCatResult;
import com.taotao.manage.mapper.ItemCatMapper;
import com.taotao.manage.pojo.ItemCat;

@Service
public class ItemCatService extends BaseService<ItemCat>{
	/*@Autowired
	private ItemCatMapper itemCatMapper;*/

	/*public List<ItemCat> querItemCatListByParentId(long pid) {
		ItemCat record = new ItemCat();
		record.setParentId(pid);
		return this.itemCatMapper.select(record);
	}*/
	
	

	/*@Override
	public Mapper<ItemCat> getMapper() {
		return this.itemCatMapper;
	}*/
	
	@Autowired
	private RedisService redisService;
	
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private static final String REDIS_KEY = "TAOTAO_MANAGE_ITEM_CAT_API"; //规则：项目名_模块名_业务名
	
	private static final Integer REDIS_TIME= 60 * 60 * 24 * 30 * 3;
	/**
	 * 全部查询 ，并且生成树状结构
	 * @return
	 */
	public ItemCatResult queryAllToTree() {
		ItemCatResult result = new ItemCatResult();
		
		
		try {
			//先从缓存Redis中命中，如果命中则返回， 没有命中继续查数据库
			String cacheData = this.redisService.get(REDIS_KEY);
			if(StringUtils.isNotEmpty(cacheData)) {
				//把json字符转为对象
				return MAPPER.readValue(cacheData, ItemCatResult.class);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//全部查出 ，并且在内存中生成树形结构 
		List<ItemCat> cats = super.queryAll();
		
		//转为map存储，key为父节点ID, value为数据集合
		Map<Long, List<ItemCat>> itemCatMap = new HashMap<Long,List<ItemCat>>();
		
		for(ItemCat itemCat : cats) {
			if(!itemCatMap.containsKey(itemCat.getParentId())) {
				itemCatMap.put(itemCat.getParentId(), new ArrayList<ItemCat>());
			}
			itemCatMap.get(itemCat.getParentId()).add(itemCat);
		}
		
		//封装一级对象
		List<ItemCat> itemCatList1 = itemCatMap.get(0L);
		for(ItemCat itemCat : itemCatList1) {
			ItemCatData itemCatData = new ItemCatData();
			itemCatData.setUrl("/products/" + itemCat.getId() + ".html");
			itemCatData.setName("<a href='" + itemCatData.getUrl() + "'>" + itemCat.getName() +"</a>");
			result.getItemCats().add(itemCatData);
			if(!itemCat.getIsParent()) {
				continue;
			}
			
			//封装二级对象
			List<ItemCat> itemCatList2 = itemCatMap.get(itemCat.getId());
			ArrayList<ItemCatData> itemCatData2 = new ArrayList<ItemCatData>();
			itemCatData.setItems(itemCatData2);
			
			for(ItemCat itemCat2 : itemCatList2) {
				ItemCatData id2 = new ItemCatData();
				id2.setName(itemCat2.getName());
				id2.setUrl("/products/" + itemCat2.getId() + ".html");
				itemCatData2.add(id2);
				if(itemCat2.getIsParent()) {
					//封装三级对象
					List<ItemCat> itemCatList3 = itemCatMap.get(itemCat2.getId());
					ArrayList<String> itemCatData3 = new ArrayList<String>();
					id2.setItems(itemCatData3);
					for(ItemCat itemCat3 : itemCatList3) {
						itemCatData3.add("/products/" + itemCat3.getId() + ".html|" + itemCat3.getName());
					}
					
				}
			}
			//因为页面高度显示菜单问题，所以限制只显示14条数据
			if(result.getItemCats().size() >= 14) {
				break;
			}
		}
		
		try {
			//将数据库查询结果集写入到缓存中
			this.redisService.set(REDIS_KEY, MAPPER.writeValueAsString(result), REDIS_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
