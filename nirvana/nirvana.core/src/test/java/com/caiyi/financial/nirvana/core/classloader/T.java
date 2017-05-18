package com.caiyi.financial.nirvana.core.classloader;

import com.caiyi.financial.nirvana.core.constant.Constant;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by wenshiliang on 2016/8/24.
 */
public class T {
    public static void main(String[] args) {
//        Constant.class;

        Class c = Constant.class;
        Field[] fields = c.getDeclaredFields();
        for(int i=0;fields!=null && i<fields.length;i++) {
            // 成员变量描述符
            String modifier = Modifier.toString(fields[i].getModifiers());
            if (modifier != null && modifier.indexOf("final")> -1) {
                // 是常量 添加到列表中/
                try {
                    System.out.println(fields[i].getName()+"--"+fields[i].get(c));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
