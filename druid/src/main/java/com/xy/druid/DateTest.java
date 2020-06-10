package com.xy.druid;

import com.mysql.jdbc.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author yefei
 */
public class DateTest {

    public static void main(String[] args) throws Exception {
        SimpleDateFormat ddf = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
        String format = ddf.format(new Date());
        System.out.println(format);
        String encoding = "gbkx";
        byte[] bytes = StringUtils.getBytes("嗷嗷", encoding);
        System.out.println(new String(bytes, encoding));

        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        System.out.println(new String(bytes, detector.getDetectedCharset()));
        System.out.println(detector.getDetectedCharset());
    }

}
