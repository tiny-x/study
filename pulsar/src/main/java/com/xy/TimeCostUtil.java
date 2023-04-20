package com.xy;

public class TimeCostUtil {

  public  static long execAndSleepTimeCost(String message,Long timeCostConst,boolean isSleep) {
    long timeCost = 0;
    String splitFlag = ":";
    if(message!=null && message.indexOf(splitFlag)>-1){
      String sendTime = message.split(splitFlag)[1];
      timeCost = System.currentTimeMillis()-Long.valueOf(sendTime);
      try {
        if(timeCost>timeCostConst && isSleep){
          Thread.sleep(timeCost);
        }
      } catch (InterruptedException e) {
      }
    }
    return timeCost;
  }
}
