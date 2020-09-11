package springbook.learningtest.template;

public interface LineCallBack<T> {
    T Accumulate(String line, T value);
}
