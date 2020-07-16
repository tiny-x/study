package com.xy.arms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;

import java.util.*;

import com.aliyuncs.arms.model.v20190808.*;


public class ARMSDemo {

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());

        Date date = new Date(1593689405000L);
        System.out.println(date);

        String str = "appstat.vm.edenSpace";

        int i = str.lastIndexOf(".");
        System.out.println(str.substring(0, i));
        System.out.println(str.substring(i + 1));

        //用户主账号或 RAM 用户的 AK，或者 RAM 用户角色的临时安全令牌的 AK。
        String accessKeyId = "LTAI4Fq4vXABDmHThqVLSAQaa9";
        //用户主账号或 RAM 用户的 SK，或者 RAM 用户角色的临时安全令牌的 SK。
        String accessKeySecret = "F1rYCxJKn4hOFY4jeKdcLlCVn6QGaa";
        //Region 和 Endpoint 保持一致，具体内容与 ARMS 的 Region 绑定。
        String region = "cn-hangzhou";//例如 cn-hangzhou
        //产品名称始终为“ARMS”。
        String productName = "ARMS";
        //如果是非杭州区域，请替换 cn-hangzhou，例如改为 cn-beijing。
        String domain = "arms.cn-hangzhou.aliyuncs.com";
        DefaultProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint(region, productName, domain);

        IAcsClient client = new DefaultAcsClient(profile);
        QueryMetricRequest request = new QueryMetricRequest();
        request.setStartTime(System.currentTimeMillis() - 3600 * 1000);
        request.setEndTime(System.currentTimeMillis());
        request.setMetric("appstat.vm");
        List<String> measuresList = new ArrayList<String>();
        measuresList.add("edenSpace");
        request.setMeasuress(measuresList);
        List<QueryMetricRequest.Filters> filtersList = new ArrayList<QueryMetricRequest.Filters>();
        QueryMetricRequest.Filters filters1 = new QueryMetricRequest.Filters();
        filters1.setKey("pid");
        filters1.setValue("dbn3xolpm3@76d56a8978f5483");
        filtersList.add(filters1);
        QueryMetricRequest.Filters filters2 = new QueryMetricRequest.Filters();
        filters2.setKey("regionId");
        filters2.setValue("cn-hangzhou");
        filtersList.add(filters2);
        request.setFilterss(filtersList);
        QueryMetricRequest.Filters filters3 = new QueryMetricRequest.Filters();
        filters3.setKey("rootIp");
        filters3.setValue("172.16.76.69");
        filtersList.add(filters3);
        request.setFilterss(filtersList);

        List<String> dimensionsList = new ArrayList<String>();
        dimensionsList.add("rootIp");
        request.setDimensionss(dimensionsList);
        try {
            QueryMetricResponse response = client.getAcsResponse(request);
            String s = JSON.toJSONString(JSONObject.parseObject(response.getData()), SerializerFeature.PrettyFormat);
            System.out.println(s);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}