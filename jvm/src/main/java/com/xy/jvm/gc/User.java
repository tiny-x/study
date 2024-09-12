package com.xy.jvm.gc;

import java.util.Date;

public class User {

    private final String name = "xf";

    private static int type = 0;

    private Date birthday;

    public static int getType() {
        return type;
    }

    public User(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * -XX:PermSize1m -XX:PermSize1m
     *
     * @param args
     */
    public static void main(String[] args) {
        String getType = new StringBuilder("get").append("Type").toString();

        System.out.println(getType.intern() == getType);

        long[][] a = {
                {1237800, 0, 178230, 29123510, 41920, 1105340, 2119370, 1224778960},
                {1242800, 0, 178370, 29158640, 41920, 1105610, 2121990, 1225428720},
                {1247900, 0, 178480, 29193340, 41920, 1105810, 2124620, 1226378320},
                {1252920, 0, 178550, 29228320, 41920, 1106020, 2127240, 1227067600},
                {1257880, 0, 178760, 29262040, 41920, 1106270, 2129870, 1227785200},
                {1262890, 0, 178950, 29297990, 41920, 1106460, 2132480, 1229144880},
                {1267960, 0, 179170, 29332710, 41920, 1106660, 2135070, 1230089040},
                {1273000, 0, 179550, 29367700, 41920, 1106890, 2137690, 1231365040},
                {1278240, 0, 180030, 29402180, 41970, 1107590, 2140300, 1232039920},
                {1283360, 0, 180330, 29437080, 41970, 1107810, 2142900, 1232883440},
                {1288370, 0, 180600, 29471900, 41970, 1108010, 2145520, 1233637040},
                {1293400, 0, 180740, 29506730, 41970, 1108230, 2148130, 1234451280},
                {1298450, 0, 180990, 29541530, 41970, 1108430, 2150740, 1235756400},
                {1303450, 0, 181090, 29576110, 41970, 1108650, 2153350, 1236533520}
        };

        long[] pre = null;
        for (long[] longs : a) {
            if (pre == null) {
                pre = longs;
                continue;
            }
            long total = 0;
            for (long aLong : longs) {
                total += aLong;
            }
            long preTotal = 0;
            for (long l : pre) {
                preTotal += l;
            }
//            System.out.println(longs[0] - pre[0]);
//            System.out.println(total - preTotal);
            System.out.println((double) (longs[0] - pre[0]) / (total - preTotal) * 100);
            System.out.println((double) (longs[2] - pre[2]) / (total - preTotal) * 100);
            System.out.println((double) (longs[4] - pre[4]) / (total - preTotal) * 100);
            System.out.println("-----------");
//            System.out.println("-------- " +  (total - preTotal)/1000000);
//
//           System.out.println((double) (longs[0] - pre[0]) / 500000000L);
//            System.out.println((double) (longs[2] - pre[2]) / 500000000L);
//            System.out.println("**********");

//            System.out.println(((longs[0]) - (pre[0]))/1000000);
        }
    }
}
