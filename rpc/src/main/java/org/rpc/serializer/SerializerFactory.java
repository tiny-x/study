package org.rpc.serializer;

public class SerializerFactory {

    public static Serializer serializer(SerializerType serializerType) {

        return new ProtoStuffSerializer();
    }
}
