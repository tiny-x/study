package serializer;

import org.rpc.serializer.ProtoStuffSerializer;

import java.util.ArrayList;
import java.util.List;

public class ProtoStuffTest {

    public static void main(String[] args) {

        List<String> strings = new ArrayList<>();
        strings.add("A");
        strings.add("B");
        strings.add("C");
        System.out.println(strings);

        ProtoStuffSerializer serializer = new ProtoStuffSerializer();


        System.out.println(serializer.deserialize(serializer.serialize(strings), ArrayList.class));

    }
}
