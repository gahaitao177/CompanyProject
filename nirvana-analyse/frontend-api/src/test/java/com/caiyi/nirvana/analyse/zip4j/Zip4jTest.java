package com.caiyi.nirvana.analyse.zip4j;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by been on 2017/1/9.
 * <p>
 * ref:  http://rensanning.iteye.com/blog/1836727
 */
public class Zip4jTest {

    private String src = new File("demo").getAbsolutePath();
    private String password = "1234";

    /**
     * 压缩文件夹
     *
     * @throws Exception
     */
    @Test
    public void zipDir() throws Exception {
        try {
            ZipFile zipFile = new ZipFile(src + ".zip");
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            //压缩
            zipFile.addFolder(src, parameters);
            String dest = new File("").getAbsolutePath();
            zipFile.extractAll(dest);
        } finally {
            Files.delete(Paths.get(src + ".zip"));
        }
    }

    @Test
    public void zipDirWithPassword() throws Exception {
        ZipFile zipFile = new ZipFile(src + ".zip");
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        // Set password
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
        parameters.setPassword(password);

        zipFile.addFolder(src, parameters);

    }


    @Test
    public void unZip() throws Exception {
        ZipFile zipFile = new ZipFile(src + ".zip");
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password);
        }
        zipFile.extractAll(new File("").getAbsolutePath());
    }

}
