package com.xy.concurrent.queue.delay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestTimeUnit {

    public static void main(String[] args) {
        //3600分钟 转换成 小时 是多少  
        System.out.println(TimeUnit.HOURS.convert(3600, TimeUnit.MINUTES));
        //3600分钟 转换成 天 是多少  
        System.out.println(TimeUnit.DAYS.convert(3600, TimeUnit.MINUTES));
        //3600分钟 转换成 秒 是多少  
        System.out.println(TimeUnit.SECONDS.convert(3600, TimeUnit.MINUTES));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //时间毫秒加减  currentTimeMillis  
        long millisTime = System.currentTimeMillis();
        long addMillisTime = millisTime + TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);
        System.out.println("current dateTime:" + sdf.format(new Date(millisTime)) + "  add 1 seconds =" + sdf.format(new Date(addMillisTime)));

        //时间纳秒加减  
        long nanoTime = System.nanoTime();
        long addNanoTime = nanoTime + TimeUnit.NANOSECONDS.convert(1, TimeUnit.MINUTES);

        System.out.println(TimeUnit.NANOSECONDS.convert(3000, TimeUnit.MILLISECONDS));
    }
} 