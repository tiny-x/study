package com.xy;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.metrics.data.DoubleGaugeData;
import io.opentelemetry.sdk.metrics.data.DoublePointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public class Main {

    // avg_disk_read_bytes_rate{instance_ip="172.17.0.3",perfma_app_code="stress_machine_app",perfma_client_id="172.17.0.3@17-7e702df7",perfma_env_id="631385292498534400",perfma_org_id="132078224594309120"} 1.4278656e+06 1726648346996
    public static void main(String[] args) throws Exception {
        long l1 = System.nanoTime();
        //111 720 481 228 125
        //447 590 619 422  7487

        OtlpGrpcMetricExporter otlpGrpcMetricExporter = OtlpGrpcMetricExporter.builder()
                .setEndpoint("http://10.10.31.20:4317")
                .build();

        Attributes attributes = Attributes.builder()
                .put("mertic_type", "cpu")
                .put("instance_ip", "172.17.0.250")
                .put("perfma_app_code", "stress_machine_app")
                .put("perfma_client_id", "172.17.0.250@17-7e702df7")
                .put("perfma_env_id", "631385292498534400")
                .put("perfma_org_id", "132078224594309120")
                .build();

        Resource resource = Resource.create(
                attributes
        );


        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long l = System.nanoTime();

            Random random = new Random();

            DoublePointData doublePointData = DoublePointData.create(l, l,
                    Attributes.of(stringKey(""), "cpu"),
                    random.nextInt(101));

            List<DoublePointData> points = new ArrayList<>();
            points.add(doublePointData);

            MetricData doubleGauge = MetricData.createDoubleGauge(
                    resource,
                    InstrumentationLibraryInfo.create("oa-agent", "1.7.0.2"),
                    "avg_whole_cpu_usage_percent",
                    "avg_whole_cpu_usage_percent",
                    "",
                    DoubleGaugeData.create(points)
            );

            ArrayList<MetricData> metricData = new ArrayList<>();
            metricData.add(doubleGauge);

            otlpGrpcMetricExporter.export(metricData);
        }
    }


}
