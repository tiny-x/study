package org.rpc.serializer;

public interface Serializer {

    SerializerType serializerType();

    <T> byte[] serialize(T object);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
