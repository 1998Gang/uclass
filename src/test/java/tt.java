import cqupt.jyxxh.uclass.utils.GetDataFromJWZX;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 7:52 2019/12/14
 */
public class tt {

    @Test
    public void skjh() throws IOException {
        CloseableHttpClient build = HttpClients.custom().build();
        HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/student/skjh.php");
        httpGet.setHeader("Cookie","PHPSESSID=ST-188066-InxA69KP1d6rUUMC6GtB-RbyM-ids1-1576676660345");
        CloseableHttpResponse execute = build.execute(httpGet);
        execute.getStatusLine().getStatusCode();
        HttpEntity entity = execute.getEntity();
        String s = EntityUtils.toString(entity, "utf-8");
        System.out.println(s);
    }

    @Test
    public void skjhParse() throws IOException {
        GetDataFromJWZX getDataFromJWZX=new GetDataFromJWZX();
        Map<String, String> cjzcByTYSF = getDataFromJWZX.getCjzcByPhpsessid("PHPSESSID=ST-188066-InxA69KP1d6rUUMC6GtB-RbyM-ids1-1576676660345");
        System.out.println(cjzcByTYSF);
    }

}
