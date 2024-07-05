import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author xf.yefei
 */
public class Script {

    public Object run(Map<String, Object> params) {
        Object returnx = params.get("return");

        try {

            Field status = returnx.getClass().getField("status");
            status.setAccessible(true);

            status.set(returnx, 500);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return returnx;
    }

}
