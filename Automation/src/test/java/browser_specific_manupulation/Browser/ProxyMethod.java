package browser_specific_manupulation.Browser;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.*;

public class ProxyMethod {
    public static void main(String[] args) throws Exception {
        HttpHost proxy = new HttpHost("38.154.227.167", 5868);
        String res = Executor.newInstance() 
            .auth(proxy, "lgzlswko", "m9s7add53nqz")
            .execute(Request.Get("http://ipv4.webshare.io/").viaProxy(proxy))
            .returnContent().asString();
        System.out.println(res);
    }
}
