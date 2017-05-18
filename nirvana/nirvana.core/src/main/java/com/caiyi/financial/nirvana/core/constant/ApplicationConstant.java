package com.caiyi.financial.nirvana.core.constant;

/**
 * Created by wenshiliang on 2016/6/8.
 * 核心包常量
 * 用来读取application.conf中配置
 */
public final class ApplicationConstant {
    /**
     *
     */
    public static String ZK_ROOT_PATH = "/hsk_nirvana";

    /**
     * springContext的名称
     */
    public static String SPRING_CONTEXT = "springContext";

    public static String SPRING_QUARTZ = "springQuartz";

    /**
     * drpc的端口地址 废弃
     */
    @Deprecated
    public static String DRPC_IP = "drpc_ip";

    /**
     * drpc服务标志唯一
     */
    public static String  DRPC_SERVICE = "drpc_service";

    /**
     * drpc的端口地址 废弃
     */
    @Deprecated
    public static String DRPC_PORT = "drpc_port";

    /**
     * 是否本地模式 废弃
     */
    @Deprecated
    public static String IS_LOCAL = "is_local";

    /**
     *  注解扫描bolt包名
     *  存在使用注解模式
     */
    public static String ANNOTATION_SCAN = "annotation_scan";

    /**
     * storm配置信息
     *
     storm : {
     numAckers = 1
     numWorkers = 1
     }
     */
    public static String STORM = "storm";

    public static class STORMClass{
        @Deprecated
        public static String NUM_ACKERS = "storm.numAckers";
        public static String NUM_WORKERS = "storm.numWorkers";
        public static String DRPC_SPOUT_NUM= "storm.drpc.spout.num";
        public static String RETURN_BOLT_NUM= "storm.return.bolt.num";
        public static String DISPATCH_BOLT_NUM= "storm.dispatch.bolt.num";
    }

    /**
     * 当ANNOTATION_SCAN获取数据为空时，使用DISPATCHER_BOLT加载Bolt
     */
    @Deprecated
    public static String DISPATCHER_BOLT = "dispatcherBolt";

    /**
     * 网银登录使用服务器配置
     */
    public static String BANK_SERVICE = "bank_service";


    /**
     * zk相关
     */


    /**
     * storm是否读取zk配置 初始化
     */
    @Deprecated
    public static String ZK_STORM_CONFIG_OPEN = "zk_storm_config_open";

    /**
     * zk地址
     */
    public static String  ZK_CONNECT = "zk_connect";
    /**
     * 启动页添加 贷款进度查询开关接口
     */
    public  static String LOAN_SWITCH="loan_switch";
}
