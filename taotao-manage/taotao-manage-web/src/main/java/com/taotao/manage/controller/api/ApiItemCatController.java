package com.taotao.manage.controller.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.ItemCatResult;
import com.taotao.manage.service.ItemCatService;

@RequestMapping("api/item/cat")
@Controller
public class ApiItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	
	/**
	 * 对外提供接口服务，查询所有类目数据 
	 * @return
	 */
	/*@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> queryItemCatList(@RequestParam(value="callback", required = false) String callback) {
		try {
			ItemCatResult itemCatResult = this.itemCatService.queryAllToTree();
			String json = MAPPER.writeValueAsString(itemCatResult);
			if(StringUtils.isEmpty(callback)) {
				return ResponseEntity.ok(json);
			}
			return ResponseEntity.ok(callback + "(" + json + ");");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
				
	}*/
	
	/**
	 * 对外提供接口服务，查询所有类目数据 (自定义 CallbackMappingJackson2HttpMessageConverter类,根据URL里是否有Callback这个变量,如果有则使用jsonP返回数据[也就是解决跨域问题] )
	 * 换句话说,在spring/taotao-manage-servlet.xml里配置自定义CallbackMappingJackson2HttpMessageConverter类后,就能支持JsonP,也就实现跨域, 只要$.ajax里 dataType:jsonp即可
	 * @param callback
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<ItemCatResult> queryItemCatList(@RequestParam(value="callback", required = false) String callback) {
		try {
			ItemCatResult itemCatResult = this.itemCatService.queryAllToTree();
				return ResponseEntity.ok(itemCatResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
				
	}
}
