package asm;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class AccessorClassLoader extends ClassLoader {

    private static final WeakHashMap<ClassLoader, WeakReference<AccessorClassLoader>> accessorClassLoaders = new WeakHashMap<>();

    private static final ClassLoader selfContextParentClassLoader = getParentClassLoader(AccessorClassLoader.class);
    private static volatile AccessorClassLoader selfContextAccessorClassLoader = new AccessorClassLoader(selfContextParentClassLoader);

    public AccessorClassLoader(ClassLoader parent) {
        super(parent);
    }

    Class<?> defineClass(String name, byte[] bytes) throws ClassFormatError {
        return defineClass(name, bytes, 0, bytes.length, getClass().getProtectionDomain());
    }

    static AccessorClassLoader get(Class<?> type) {
        ClassLoader parent = getParentClassLoader(type);

        // 1. 最快路径:
        if (selfContextParentClassLoader.equals(parent)) {
            if (selfContextAccessorClassLoader == null) {
                synchronized (accessorClassLoaders) { // DCL with volatile semantics
                    if (selfContextAccessorClassLoader == null)
                        selfContextAccessorClassLoader = new AccessorClassLoader(selfContextParentClassLoader);
                }
            }
            return selfContextAccessorClassLoader;
        }

        // 2. 常规查找:
        synchronized (accessorClassLoaders) {
            WeakReference<AccessorClassLoader> ref = accessorClassLoaders.get(parent);
            if (ref != null) {
                AccessorClassLoader accessorClassLoader = ref.get();
                if (accessorClassLoader != null) {
                    return accessorClassLoader;
                } else {
                    accessorClassLoaders.remove(parent); // the value has been GC-reclaimed, but still not the key (defensive sanity)
                }
            }
            AccessorClassLoader accessorClassLoader = new AccessorClassLoader(parent);
            accessorClassLoaders.put(parent, new WeakReference<>(accessorClassLoader));
            return accessorClassLoader;
        }
    }

    private static ClassLoader getParentClassLoader(Class<?> type) {
        ClassLoader parent = type.getClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return parent;
    }
}