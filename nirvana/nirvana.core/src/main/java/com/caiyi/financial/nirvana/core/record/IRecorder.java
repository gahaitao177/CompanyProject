package com.caiyi.financial.nirvana.core.record;

/**
 * Created by Mario on 2016/11/30 0030.
 * 服务器请求记录器
 */
@SuppressWarnings("unused")
interface IRecorder {
    void record(Object data);
}
