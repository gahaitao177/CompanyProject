package com.caiyi.financial.nirvana.http.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Created by wenshiliang on 2016/12/12.
 */
public class NirvanaStringHttpMessageConverter extends org.springframework.http.converter.StringHttpMessageConverter {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final Charset defaultCharset;

    public NirvanaStringHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    public NirvanaStringHttpMessageConverter(Charset defaultCharset) {
        super(defaultCharset);
        this.defaultCharset = defaultCharset;
    }


    @Override
    protected void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
//        System.out.println("asdfasdf");
        if(outputMessage instanceof ServletServerHttpResponse){
            HttpServletResponse response = ((ServletServerHttpResponse) outputMessage).getServletResponse();
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
            Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
            StreamUtils.copy(str, charset, response.getOutputStream());
        }else{
            super.writeInternal(str,outputMessage);
        }


//        super.writeInternal(str, outputMessage);
//        PrintWriter writer = response.getWriter();
//        writer.print(str);
//        writer.flush();
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            return contentType.getCharSet();
        }
        else {
            return this.defaultCharset;
        }
    }




}
