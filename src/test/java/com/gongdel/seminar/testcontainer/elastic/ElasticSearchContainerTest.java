package com.gongdel.seminar.testcontainer.elastic;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchContainerTest {

	@Test
	public void test() throws IOException
	{
		ElasticsearchContainer container
				= new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.12.0");
		container.start();

		BasicCredentialsProvider credentialProvider = new BasicCredentialsProvider();
		credentialProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials("elasticsearch", "elasticsearch"));

		RestClientBuilder builder = RestClient.builder(HttpHost.create(container.getHttpHostAddress()))
				.setHttpClientConfigCallback(
						httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialProvider)
				);

		RestHighLevelClient client = new RestHighLevelClient(builder);
		// index생성전 index가 존재하는지 확인
		boolean isIndexExists = client.indices().exists(new GetIndexRequest("test_index"), RequestOptions.DEFAULT);
		assertThat(isIndexExists).isEqualTo(false);

		client.indices().create(new CreateIndexRequest("test_index"), RequestOptions.DEFAULT);

		// index생성후 index가 존재하는지 확인
		isIndexExists = client.indices().exists(new GetIndexRequest("test_index"), RequestOptions.DEFAULT);
		assertThat(isIndexExists).isEqualTo(true);
	}
}
