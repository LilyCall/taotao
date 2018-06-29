package com.taotao.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemDescService;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class HtmlGenListener implements MessageListener {

	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDescService itemDescService;
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Value("${HTML_OUT_PATH}")
	private String HTML_OUT_PATH;
	
	@Override
	public void onMessage(Message message) {
		try {
			// 1、创建一个MessageListener接口的实现类
			// 2、从message中取商品id
			TextMessage textMessage = (TextMessage) message;
			String strItemId = textMessage.getText();
			if (StringUtils.isNotBlank(strItemId)){
				
				String[] strings = strItemId.split("-");
				Long itemId = new Long(strings[0]);
				
				// 3、查询商品基本消息、商品描述。
				TbItem tbItem = itemService.getItemById(itemId);
				Item item = new Item(tbItem);
				TbItemDesc tbItemDesc = itemDescService.findItemDescById(itemId);
				//创建数据集
				Map data = new HashMap<>();
				data.put("item", item);
				data.put("itemDesc", tbItemDesc);
				// 4、创建商品详情页面的模板。
				// 5、指定文件输出目录
				Configuration configuration = freeMarkerConfigurer.getConfiguration();
				Template template = configuration.getTemplate("item.htm");
				
				FileWriter out = new FileWriter(new File(HTML_OUT_PATH + itemId + ".html"));
				// 6、生成静态文件。
				template.process(data, out);
				//关闭流
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
