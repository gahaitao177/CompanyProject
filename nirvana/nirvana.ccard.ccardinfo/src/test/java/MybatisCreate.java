import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wsl on 2015/12/31.
 */
public class MybatisCreate {

    public static void main(String[] args) throws Exception {

//        MybatisCreate.getResource("/")
        String str = MybatisCreate.class.getClass().getResource("/").getFile().toString();
//        System.out.println(str);
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File(str+"\\generatorConfig.xml");


        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGeneratore = new MyBatisGenerator(config, callback, warnings);
        myBatisGeneratore.generate(null);
    }
}
