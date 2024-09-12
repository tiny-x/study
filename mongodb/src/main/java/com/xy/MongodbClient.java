package com.xy;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.function.Consumer;

public class MongodbClient {

    MongoClient mongoClient;
    MongoDatabase database;

    private String mondbUrl = "mongodb://admin:123456@10.10.220.46:27017/";

    public void getConnection() {
        ConnectionString connectionString = new ConnectionString(mondbUrl);
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("test");
        try {
            //database.createCollection("test");
        } catch (Exception e) {
            //忽略异常，如果collection已经存在
        }
    }
    /**
     *
     */
    public void mockMongodb() {
        com.mongodb.client.MongoCollection<Document> collection = database.getCollection("test");
        String databaseName = collection.getNamespace().getDatabaseName();
        String collectionName = collection.getNamespace().getCollectionName();
        Document doc1 = new Document("color", "red").append("qty", 5);
        collection.insertOne(doc1);
        collection.find().forEach((Consumer<? super Document>) doc -> System.out.println("mongodb查询数据：" + doc.toJson()));
        Bson filter = Filters.eq("qty", 5);
        collection.deleteMany(filter);

    }

}

