package com.atguigu.gmall.list;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServerApplicationTests {

	@Autowired
	private JestClient jestClient;

	/**
	 * 定义dsl语句
	 * 定义要执行的动作
	 * jestClient执行动作
	 * 获取返回的结果
	 */

	@Test
	public void contextLoads() {

	}

	@Test
	public void TestEs() throws IOException {
     //定义一个dsl语句
		String query = "{\n" +
				"  \"query\": {\n" +
				"    \"bool\": {\n" +
				"      \"filter\": {\n" +
				"        \"term\": {\n" +
				"          \"actorList.name\": \"张译\"\n" +
				"        }\n" +
				"      }\n" +
				"    }\n" +
				"  }\n" +
				"}";
		//定义要执行的动作
		Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie_type_chn").build();
		//jestClient执行动作
		SearchResult result = jestClient.execute(search);
		//获取返回的结果
		List<SearchResult.Hit<HashMap, Void>> hits = result.getHits(HashMap.class);
		for (SearchResult.Hit<HashMap, Void> hit : hits) {
			HashMap source = hit.source;
			Object name = source.get("name");
			System.out.println(name);
		}


	}

}
