package com.caiyi.nirvana.analyse.monitor.meters;

/**
 * Created by pc on 2017/3/10.
 */
public interface MetersCallback<T> {

    void report(T t);

}
