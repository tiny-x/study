package com.xy.exception;

/**
 * @author yefei
 * @date 2018-03-02 16:19
 */
public class ExceptionExample {

    public static void main(String[] args) {

        AnyThrow.throwAny(new Exception("aa"));
    }

    static class AnyThrow {
        public static void throwUnchecked(Throwable e) {
            AnyThrow.<RuntimeException>throwAny(e);
        }

        @SuppressWarnings("unchecked")
        private static <E extends Throwable> void throwAny(Throwable e) throws E {
            throw (E) e;
        }
    }
}
