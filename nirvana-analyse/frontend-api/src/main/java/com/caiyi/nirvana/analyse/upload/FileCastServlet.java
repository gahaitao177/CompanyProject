package com.caiyi.nirvana.analyse.upload;

import com.mina.rbc.util.DateUtil;
import com.rbc.frame.Globals;
import com.rbc.frame.ServiceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FileCastServlet extends HttpServlet {

    private static Logger logger = LogManager.getLogger("fileupload");
    private static final long serialVersionUID = -6465051765821264099L;

    private static final String UID_KEY = "uid";
    private static final String PWD_KEY = "pwd";
    private static final String ENCODING = "UTF-8";


    public static final String PATH = "/";
    private final String UPFILE = "upfile";

    ServiceContext context = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request
     * servlet request
     * @param response
     * servlet response
     */
    private int maxPostSize = 50 * 1024 * 1024;

    public FileCastServlet() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        context = (ServiceContext) this.getServletConfig().getServletContext().getAttribute(Globals.RBC_SERVICE_CONTEXT);
        PrintWriter out = null;

        try {
            request.setCharacterEncoding(ENCODING);
            response.setContentType("text/html;charset=" + ENCODING);
            response.setCharacterEncoding(ENCODING);
            out = response.getWriter();
            // 检查是否登录
            long ustime = System.currentTimeMillis();
            logger.info("开始上传：" + DateUtil.getCurrentDateTime());
            try {// 构造对象
                FileUpload upload = new FileUpload(request, PATH, new String[]{"txt"}, this.maxPostSize);
            } catch (Exception e) {
                throw new Exception("上传发生错误");
            }
            long uetime = System.currentTimeMillis();
            logger.info("上传完成：" + DateUtil.getCurrentDateTime());
            logger.info("上传耗时：" + (uetime - ustime) / 1000 + "秒");
            logger.info("文件保存位置：" + PATH);
            // 获取参数
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "file upload servlet";
    }

}