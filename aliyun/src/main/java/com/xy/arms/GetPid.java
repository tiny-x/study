package com.xy.arms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.arms.model.v20190808.ListTraceAppsRequest;
import com.aliyuncs.arms.model.v20190808.ListTraceAppsResponse;
import com.aliyuncs.profile.DefaultProfile;

import java.util.List;

/**
 * @author yefei
 * @create 2020-06-11 16:01
 */
public class GetPid {

    public static void main(String[] args) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4Fq4vXABDmHThqVLSAQ9", "F1rYCxJKn4hOFY4jeKdcLlCVn6QGaa");
        IAcsClient client = new DefaultAcsClient(profile);

        ListTraceAppsRequest request = new ListTraceAppsRequest();
        request.setRegionId("cn-hangzhou");

        try {
            ListTraceAppsResponse response = client.getAcsResponse(request);
            List<ListTraceAppsResponse.TraceApp> traceApps = response.getTraceApps();
            for (ListTraceAppsResponse.TraceApp traceApp : traceApps) {
                System.out.println(traceApp.getAppName() + "\t" + traceApp.getPid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
