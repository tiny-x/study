package com.xy;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class ElasticTest {

    public static RestHighLevelClient esClient;

    static {
        esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("10.10.225.128", 9200, "http"))
        );
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long start = System.currentTimeMillis();
            getIndex();
            System.out.printf("耗时 %d \n", System.currentTimeMillis() - start);
        }
    }

    /**
     * 创建索引
     *
     * @throws IOException
     */
    public static void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("user");
        CreateIndexResponse indexResponse = esClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        boolean acknowledged = indexResponse.isAcknowledged();
        System.out.println("索引创建状态:" + acknowledged);
    }

    /**
     * 索引信息查询
     *
     * @throws IOException
     */
    public static void getIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest("user");
        GetIndexResponse getIndexResponse = esClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(getIndexResponse.getAliases());
        System.out.println(getIndexResponse.getMappings());
        System.out.println(getIndexResponse.getSettings());
    }

    /**
     * 删除索引
     *
     * @throws IOException
     */
    public static void deleteIndex() throws IOException {
        DeleteIndexRequest getIndexRequest = new DeleteIndexRequest("user");
        AcknowledgedResponse delete = esClient.indices().delete(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println("索引删除状态:" + delete.isAcknowledged());
    }
}
