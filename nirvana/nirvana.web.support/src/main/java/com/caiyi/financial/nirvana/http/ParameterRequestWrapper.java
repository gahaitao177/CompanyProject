package com.caiyi.financial.nirvana.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/8/19.
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String,String[]> map = new HashMap<>();

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public ParameterRequestWrapper(HttpServletRequest request) {
        super(request);

        map.putAll(request.getParameterMap());
    }


    @Override
    public String[] getParameterValues(String name) {
        return map.get(name);
    }

    @Override
    public Enumeration getParameterNames() {
        return new ParamEnumeration(map.keySet().iterator());
    }

    @Override
    public Map getParameterMap() {
        return map;
    }

    @Override
    public String getParameter(String name) {
        String[] values = map.get(name);
        if(values!=null && values.length>0){
            return values[0];
        }
        return null;
    }

    /**
     * 重写servlet 添加setParamter方法
     * @param name
     * @param values
     */
    public void setParameter(String name,String... values){
        map.put(name,values);
    }

    static class ParamEnumeration implements Enumeration<String>{

        public ParamEnumeration(Iterator<String> iterator) {
            this.iterator = iterator;
        }

        private Iterator<String> iterator;

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public String nextElement() {
           return iterator.next();
        }
    }
}
