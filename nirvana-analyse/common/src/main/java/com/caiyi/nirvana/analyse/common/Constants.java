package com.caiyi.nirvana.analyse.common;


public class Constants {
    public static boolean PROD = false;
    public static String FILEPREFIX = PROD ? "/opt/export/data/jz" : "/opt/export/data"; //文件访问地址

    public final static String CASSANDRA_KEYSPACE_NAME = "cassandra.keyspace.name";

    public final static String CASSANDRA_CANTRACT_POINT = "cassandra.keyspace.name";
}
