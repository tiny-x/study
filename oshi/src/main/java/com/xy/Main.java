package com.xy;


import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Main {

    public static void main(String[] args) throws Exception {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();


        System.out.println("操作系统：" + operatingSystem.toString());
        int physicalProcessorCount = hardwareAbstractionLayer.getProcessor().getPhysicalProcessorCount();
        System.out.println("cpu 核心数：" + physicalProcessorCount);
        System.out.println("cpu 逻辑核心数：" + hardwareAbstractionLayer.getProcessor().getLogicalProcessors());

        long[][] pre = hardwareAbstractionLayer.getProcessor().getProcessorCpuLoadTicks();

        long preTimestamp = System.currentTimeMillis();
        while (true) {
            Thread.sleep(1000L);

            long[][] current = hardwareAbstractionLayer.getProcessor().getProcessorCpuLoadTicks();
            long currentTimestamp = System.currentTimeMillis();

            double total = 0L;
            for (int i = 0; i < pre.length; i++) {
                double usage = usage(current[i], pre[i], currentTimestamp - preTimestamp);
                total = total + usage;
            }

            // 得除物理核心才能跟 topas 同步
            System.out.println("CPU 使用率：" + total * 100 / physicalProcessorCount);
            // 更新上一次
            pre = current;
            preTimestamp = currentTimestamp;
        }
    }

    // 计算单核（user+sys+io 不含中断等其他）

    /**
     * User (0), Nice (1), System (2), Idle (3), IOwait (4), Hardware interrupts (IRQ) (5), Software interrupts/DPC
     * * (SoftIRQ) (6), or Steal (7) states.
     *
     * @param current
     * @param pre
     * @param timDiff
     * @return
     */
    public static double usage(long[] current, long[] pre, long timDiff) {
        BigDecimal bigDecimal = new BigDecimal((current[0] + current[2] + current[4]) - (pre[0] + pre[2] + pre[4]));
        BigDecimal divide = bigDecimal.divide(new BigDecimal(timDiff), 2, RoundingMode.HALF_UP);
        return divide.doubleValue();
    }

}
