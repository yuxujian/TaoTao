package com.taotao.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.web.bean.Item;


@Service
public class ItemService {
	@Autowired
	private ApiService apiService;
	
	@Value("${TAOTAO_MANAGE_URL}")
	private String TAOTAO_MANAGE_URL;
	
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	
	/**
	 * 根据商品id查询商品数据 
	 * 
	 * 通过后台系统 提供的接口服务进行查询
	 * @param itemId
	 * @return
	 */
	public Item queryById(Long itemId) {
		String url = TAOTAO_MANAGE_URL + "/rest/api/item/" + itemId;
		
		try {
			String jsonData = this.apiService.doGet(url);
			if(StringUtils.isEmpty(jsonData)) {
				return null;
			}
			
			//将json数据 反序列为Item对象
			return MAPPER.readValue(jsonData, Item.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}

}
