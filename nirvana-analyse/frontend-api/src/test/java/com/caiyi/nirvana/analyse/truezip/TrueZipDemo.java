package com.caiyi.nirvana.analyse.truezip;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;
import de.schlichtherle.truezip.file.TFileWriter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;

/**
 * Created by been on 2016/12/29.
 */
public class TrueZipDemo {
    @Test
    public void testRead() {
        File file = new TFile("test.zip/demo/hello.txt");
        read(file);
    }

    private void read(File file) {
        try {
            Reader reader = new TFileReader(file);
            String str = IOUtils.toString(reader);
            System.out.println(str);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWrite() {
        File entry = new TFile("test.zip/demo/hello.txt");
        write(entry);
    }

    private void write(File entry) {
        try {
            //是否追加
            boolean append = true;
            Writer writer = new TFileWriter(entry, append);
            writer.write("hello world\n");
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void extractEncypedFile() {
        File entry = new TFile("pom.zip/demo/test.txt");
        write(entry);
//        read(entry);
    }
}
