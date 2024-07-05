import cn.hutool.core.util.HexUtil;
import com.ghgande.j2mod.modbus.io.BytesInputStream;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;

import java.util.Base64;


//我解析的是这个数据：0103 200c 3f0c 3f0c 790c 4e0c 450c 4b0c 470c 4f0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ffac f2

public class RM417DataParser {

    public static void main(String[] args) throws Exception {
        String c = "AQMgDDMMnQxfDC8MKwwlDCcMKQ//D/8P/w//D/8P/w//D/8KgQ==";
        byte[] rawModbusData = Base64.getDecoder().decode(c.getBytes());

        String replace = "0303 200f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f ff0f fffc 85".replace(" ", "");
        byte[] bytes = HexUtil.decodeHex(replace);
        // 原始Modbus RTU数据
        //byte[] rawModbusData = encode;

        // 忽略CRC校验部分，实际应用中需要添加CRC校验及验证
        ReadMultipleRegistersResponse response = parseModbusResponse(bytes);

        for (int i = 0; i < response.getWordCount(); i++) {
            int registerValue = response.getRegisterValue(i);
            System.out.println(registerValue);
            double voltageOrCurrent = convertToPhysicalValue(registerValue); // 转换函数实现见下方
            System.out.printf("通道 %d 的模拟量值: %.4f V 或 %.4f mA%n", i + 13, voltageOrCurrent, voltageOrCurrent * scalingFactorForCurrent());
        }
    }

    private static ReadMultipleRegistersResponse parseModbusResponse(byte[] data) throws Exception {
        BytesInputStream stream = new BytesInputStream(data);
        ReadMultipleRegistersResponse response = new ReadMultipleRegistersResponse();
        response.setHeadless();
        response.readFrom(stream);
        return response;
    }

    // 实现这个函数以根据不同的输入范围转换原始值到实际电压或电流值
    private static double convertToPhysicalValue(int registerValue) {
        // 这里假设0-5V输入，根据文档提供的转换表格进行转换
        double rangeMax = 5.0; // 5V
        double resolution = rangeMax / 4095.0;
        return registerValue * resolution;
    }

    // 如果输入类型为电流，根据配置提供合适的比例因子
    private static double scalingFactorForCurrent() {
        // 假设0-20mA输入对应0-5V，比例因子为0.0025
        return 0.0025; // 单位为A/V
    }
}

// 注意：以上代码没有包含完整的错误处理机制，实际编程时需要增加异常捕获和其他必要检查。
