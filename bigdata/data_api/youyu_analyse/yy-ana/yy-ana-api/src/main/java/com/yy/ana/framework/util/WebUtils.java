package com.yy.ana.framework.util;

import com.yy.ana.domain.BaseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by User on 2017/5/31.
 */
public class WebUtils {

    public static BaseDto getPraramsAsDto(HttpServletRequest request) {
        BaseDto dto = new BaseDto();
        Map map = request.getParameterMap();
        Iterator keyIterator = map.keySet().iterator();

        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            String value = "";
            if (((String[]) ((String[]) map.get(key))).length > 0) {
                String[] arr$ = (String[]) ((String[]) map.get(key));
                int len$ = arr$.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    String v = arr$[i$];
                    value = value + v + ",";
                }

                dto.put(key, value.substring(0, value.length() - 1));
            } else {
                value = ((String[]) ((String[]) map.get(key)))[0];
                dto.put(key, value.trim());
            }
        }

        return dto;
    }
}
