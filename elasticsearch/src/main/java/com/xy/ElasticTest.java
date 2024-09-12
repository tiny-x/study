package com.xy;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class ElasticTest {

    public static RestHighLevelClient esClient;

    static {
        esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("10.10.222.157", 9200, "http"))
        );
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long start = System.currentTimeMillis();
            System.out.printf("耗时 %d \n", System.currentTimeMillis() - start);

            IndexRequest indexRequest = new IndexRequest("monitor_node_v3-2024.08.22-000043")
                    .id("node_table_791264627161038848_10.10.220.46@12431b2f_xchaos-1.perfma-inc.com")
                    .source("{\"avgDiskReadBytesRate\":0,\"avgDiskReads\":214735.45,\"avgDiskUsagePercent\":0.91," +
                            "\"avgDiskWriteBytesRate\":73088,\"avgDiskWrites\":18.5,\"avgMemUsagePercent\":0.46552,\"avgNetReceiveBytesRate\":64237.25,\"" +
                            "avgNetSendBytesRate\":43376,\"avgNewDiskIoUsagePercent\":0.22,\"avgWaitCpuUsagePercent\":0.002028,\"avgWholeCpuUsage" +
                            "Percent\":0.023574,\"contextSwitch\":14062,\"count\":1,\"delayTime\":0.03,\"diskIoTimeProportion\":\"[{\\" +
                            "\"diskName\\\":\\\"sr0\\\",\\\"ioProportion\\\":0},{\\\"diskName\\\":\\\"sda\\\",\\\"io" +
                            "Proportion\\\":0.93},{\\\"diskName\\\":\\\"sda1\\\",\\\"ioProportion\\\":0.93}]\",\"di" +
                            "skWriteSpendTime\":\"[{\\\"diskName\\\":\\\"sr0\\\",\\\"writeSpendTime\\\":0},{\\\"diskName" +
                            "\\\":\\\"sda\\\",\\\"writeSpendTime\\\":1.84},{\\\"diskName\\\":\\\"sda1\\\",\\\"writeSpendTi" +
                            "me\\\":2.49}]\",\"disks\":\"{\\\"/dev/sda\\\":{\\\"avgDiskReadBytesRate\\\":0,\\\"avgDiskReads\\" +
                            "\":0,\\\"avgDiskWriteBytesRate\\\":73088,\\\"avgDiskWrites\\\":18.5,\\\"name\\\":\\\"/dev/sda\\\"}}\",\"" +
                            "hostName\":\"xchaos-1.perfma-inc.com\",\"id\":\"node_791264627161038848_10.10.220.46@12431b2f_xchaos-1.perfma-i" +
                            "nc.com_1724383745000_1724383750000\",\"instance\":\"10.10.220.46@12431b2f\",\"maxFileDescriptors\":0,\"maxMemUsa" +
                            "gePercent\":0.46552,\"maxWholeCpuUsagePercent\":0.023574,\"memAvailable\":8902115328,\"memTotal\":16655671296," +
                            "\"mesh\":-1,\"minMemUsagePercent\":0.46552,\"minWholeCpuUsagePercent\":0.023574,\"nodeCpuLogicalSize\":8" +
                            ",\"nodeCpuPhysicalSize\":8,\"nodeLoad1\":0.24,\"nodeLoad15\":0.53,\"nodeLoad5\":0.37,\"nodeMemTotal\":16655" +
                            "671296,\"openFileDescriptors\":4192,\"reportId\":791264627161038848,\"sumCount\":1,\"tcpV4StatsConnectionsActive\":0" +
                            ",\"tcpV4StatsConnectionsEstablished\":53,\"tcpV4StatsConnectionsPassive\":1,\"ts\":1724383745000,\"virtualMem" +
                            "oryTotal\":0,\"virtualMemoryUsed\":0}", XContentType.JSON);

            IndexResponse index = esClient.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println(index.toString());
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
