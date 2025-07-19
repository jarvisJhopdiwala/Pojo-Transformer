package io.github.syphen.utils;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
} 