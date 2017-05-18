import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.RemoteDrpcClient;

/**
 * Created by Mario on 2016/7/26 0026.
 */
public class Mario {
    public static void main(String[] args) {
        RemoteDrpcClient rdc = new RemoteDrpcClient("192.168.1.207",3772,"hskCInvestigation");
        Channel bean = new Channel();
        bean.setBusiErrCode(-1);
        bean.setBusiErrDesc("Mario");
        bean.setCuserId("yrk");
        String res = rdc.execute(new DrpcRequest("investLogin","checkReportExists",bean));
        System.out.println(res);

        bean.setLoginname("yaoruikang9188");
        //res = rdc.execute(new DrpcRequest("investReport","updateReApplyStatus",bean));
        //System.out.println(res);


    }
}
