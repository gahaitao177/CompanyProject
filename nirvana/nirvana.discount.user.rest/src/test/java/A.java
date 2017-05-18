import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by wenshiliang on 2016/9/7.
 */
public class A {
    public static void main(String[] args) throws SocketException {
        System.out.println("192.168.1.127".indexOf("127"));
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//            System.out.println(netInterface.getName());
            /*
            
            本机的IP = 127.0.0.1
本机的IP = 169.254.128.232
本机的IP = 169.254.202.11
本机的IP = 192.168.10.29
本机的IP = 10.0.83.13
             */
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address)
                {
                    String ipStr = ip.getHostAddress();
                    if(ipStr.indexOf("127")!=0){
                        System.out.println("本机的IP = " + ipStr);
                    }

                }
            }
        }
    }
}
