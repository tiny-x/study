package com.xy;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;

public class HbaseHelper {

    // 建表 args-->指定列族
    public void createTable(Connection connection, String tableName, String... args) throws IOException {
        Admin admin = connection.getAdmin();
        TableDescriptorBuilder tdesBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        ArrayList<ColumnFamilyDescriptor> cflist = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            ColumnFamilyDescriptor cf = ColumnFamilyDescriptorBuilder.of(args[i]);
            cflist.add(cf);
        }
        tdesBuilder.setColumnFamilies(cflist);
        TableDescriptor table = tdesBuilder.build();
        if (admin.tableExists(TableName.valueOf(tableName))) {
            System.out.println("Table is exists.");
        } else {
            System.out.println("Creating table. ");
            admin.createTable(table);
        }
        admin.close();
    }

    // 删表
    public void deleteTable(Connection connection, String tableName) throws IOException {
        Admin admin = connection.getAdmin();

        if (admin.tableExists(TableName.valueOf(tableName))) {
            if(admin.isTableEnabled(TableName.valueOf(tableName))){
                admin.disableTable(TableName.valueOf(tableName));
            }
            System.out.println("Delete table. ");
            admin.deleteTable(TableName.valueOf(tableName));
        }else {
            System.out.println("Table is not exists.");
        }

        admin.close();
    }

    /**
     *插入数据
     */
    public void putRows(Connection connection, String tableName, String... args) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        ArrayList<Put> puts = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String[] row = args[i].split(":");
            Put put = new Put(Bytes.toBytes(row[0]));
            put.addColumn(Bytes.toBytes(row[1]), Bytes.toBytes(row[2]),
                    Bytes.toBytes(row[3]));
            puts.add(put);
        }
        table.put(puts);
        table.close();
    }

    public void getRows(Connection connection, String tableName, String rowkey) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowkey));
        Result result = table.get(get);
        for (Cell cell : result.rawCells()) {
            byte[] rk = CellUtil.cloneRow(cell);
            byte[] family = CellUtil.cloneFamily(cell);
            byte[] column = CellUtil.cloneQualifier(cell);
            byte[] value = CellUtil.cloneValue(cell);
            String kv = Bytes.toString(rk) + ":" + Bytes.toString(family) + ":" + Bytes.toString(column) + ":" + Bytes.toString(value);
            System.out.println(kv);
        }
        table.close();
    }
}
