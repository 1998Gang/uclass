
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 7:52 2019/12/14
 */
public class tt {

    private Object TypeReference;

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

    }



    //测试嵌套集合
    @Test
    public void ttttt() throws JsonProcessingException {
        String ss="[[[{\"jxb\":\"A12191A2120310003\",\"kch\":\"A2120310\",\"kcm\":\"电视节目策划\",\"skzs\":\"1-16周\",\"skdd\":\"8121\",\"jsm\":\"谢盼盼\",\"kclb\":\"必修\",\"credit\":\"2.0学分\",\"xklx\":\"跨专业\",\"bjlbandbjh\":null,\"week\":\"11111111111111110000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\"],\"weekday\":\"1\",\"cStart\":\"1\",\"cTimes\":\"2\",\"cjzc\":null},{\"jxb\":\"SJ13191A2130640003\",\"kch\":\"A2130640\",\"kcm\":\"课程设计（软件分析与设计）\",\"skzs\":\"17周\",\"skdd\":\"综合实验楼A503/A504\",\"jsm\":\"王正军\",\"kclb\":\"必修\",\"credit\":\"1.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000000000001000\",\"weekNum\":[\"17\"],\"weekday\":\"1\",\"cStart\":\"1\",\"cTimes\":\"4\",\"cjzc\":null}],[{\"jxb\":\"SJ13191A2130640003\",\"kch\":\"A2130640\",\"kcm\":\"课程设计（软件分析与设计）\",\"skzs\":\"17周\",\"skdd\":\"综合实验楼A503/A504\",\"jsm\":\"王正军\",\"kclb\":\"必修\",\"credit\":\"1.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000000000001000\",\"weekNum\":[\"17\"],\"weekday\":\"2\",\"cStart\":\"1\",\"cTimes\":\"4\",\"cjzc\":null}],[{\"jxb\":\"A13191A2130520001\",\"kch\":\"A2130520\",\"kcm\":\"人机交互技术及应用\",\"skzs\":\"1-8周\",\"skdd\":\"3212\",\"jsm\":\"叶晓静\",\"kclb\":\"选修\",\"credit\":\"3.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"11111111000000000000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\"],\"weekday\":\"3\",\"cStart\":\"1\",\"cTimes\":\"2\",\"cjzc\":null},{\"jxb\":\"SJ13191A2130640003\",\"kch\":\"A2130640\",\"kcm\":\"课程设计（软件分析与设计）\",\"skzs\":\"17周\",\"skdd\":\"综合实验楼A503/A504\",\"jsm\":\"王正军\",\"kclb\":\"必修\",\"credit\":\"1.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000000000001000\",\"weekNum\":[\"17\"],\"weekday\":\"3\",\"cStart\":\"1\",\"cTimes\":\"4\",\"cjzc\":null}],[{\"jxb\":\"SJ13191A2130640003\",\"kch\":\"A2130640\",\"kcm\":\"课程设计（软件分析与设计）\",\"skzs\":\"17周\",\"skdd\":\"综合实验楼A503/A504\",\"jsm\":\"王正军\",\"kclb\":\"必修\",\"credit\":\"1.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000000000001000\",\"weekNum\":[\"17\"],\"weekday\":\"4\",\"cStart\":\"1\",\"cTimes\":\"4\",\"cjzc\":null}],[{\"jxb\":\"SJ13191A2130640003\",\"kch\":\"A2130640\",\"kcm\":\"课程设计（软件分析与设计）\",\"skzs\":\"17周\",\"skdd\":\"综合实验楼A503/A504\",\"jsm\":\"王正军\",\"kclb\":\"必修\",\"credit\":\"1.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000000000001000\",\"weekNum\":[\"17\"],\"weekday\":\"5\",\"cStart\":\"1\",\"cTimes\":\"4\",\"cjzc\":null},{\"jxb\":\"A13191A2130060001\",\"kch\":\"A2130060\",\"kcm\":\"多媒体技术及应用\",\"skzs\":\"1-4周\",\"skdd\":\"2308\",\"jsm\":\"赵志强\",\"kclb\":\"选修\",\"credit\":\"1.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"11110000000000000000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\"],\"weekday\":\"5\",\"cStart\":\"1\",\"cTimes\":\"2\",\"cjzc\":null}],[],[]],[[{\"jxb\":\"A13191A2130210001\",\"kch\":\"A2130210\",\"kcm\":\"数据与算法基础 (2) (数据库) \",\"skzs\":\"1周,4-18周\",\"skdd\":\"2406\",\"jsm\":\"刘歆\",\"kclb\":\"必修\",\"credit\":\"3.0学分\",\"xklx\":\"重修\",\"bjlbandbjh\":null,\"week\":\"10011111111111111100\",\"weekNum\":[\"1\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\",\"17\",\"18\"],\"weekday\":\"1\",\"cStart\":\"3\",\"cTimes\":\"2\",\"cjzc\":null}],[{\"jxb\":\"SK13191A2130610002\",\"kch\":\"A2130610\",\"kcm\":\"软件过程与项目管理\",\"skzs\":\"9-16周\",\"skdd\":\"综合实验楼B415/B416\",\"jsm\":\"万邦睿\",\"kclb\":\"必修\",\"credit\":\".0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000111111110000\",\"weekNum\":[\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\"],\"weekday\":\"2\",\"cStart\":\"3\",\"cTimes\":\"2\",\"cjzc\":null},{\"jxb\":\"A13191A2130610002\",\"kch\":\"A2130610\",\"kcm\":\"软件过程与项目管理\",\"skzs\":\"1-8周\",\"skdd\":\"4404\",\"jsm\":\"万邦睿\",\"kclb\":\"必修\",\"credit\":\"2.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"11111111000000000000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\"],\"weekday\":\"2\",\"cStart\":\"3\",\"cTimes\":\"2\",\"cjzc\":null}],[{\"jxb\":\"A13191A2130440002\",\"kch\":\"A2130440\",\"kcm\":\"编译技术\",\"skzs\":\"1-16周\",\"skdd\":\"3401\",\"jsm\":\"范时平\",\"kclb\":\"必修\",\"credit\":\"2.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"11111111111111110000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\"],\"weekday\":\"3\",\"cStart\":\"3\",\"cTimes\":\"2\",\"cjzc\":null}],[{\"jxb\":\"A13191A2130350001\",\"kch\":\"A2130350\",\"kcm\":\"计算机网络\",\"skzs\":\"2-6周双周,10-12周双周\",\"skdd\":\"4515\",\"jsm\":\"曹岭\",\"kclb\":\"必修\",\"credit\":\"3.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"01010100010100000000\",\"weekNum\":[\"2\",\"4\",\"6\",\"10\",\"12\"],\"weekday\":\"4\",\"cStart\":\"3\",\"cTimes\":\"2\",\"cjzc\":null}],[{\"jxb\":\"A04191A1110320006\",\"kch\":\"A1110320\",\"kcm\":\"大学物理B（下）\",\"skzs\":\"1-15周单周\",\"skdd\":\"3204\",\"jsm\":\"柴昭\",\"kclb\":\"必修\",\"credit\":\"3.0学分\",\"xklx\":\"重修\",\"bjlbandbjh\":null,\"week\":\"10101010101010100000\",\"weekNum\":[\"1\",\"3\",\"5\",\"7\",\"9\",\"11\",\"13\",\"15\"],\"weekday\":\"5\",\"cStart\":\"3\",\"cTimes\":\"2\",\"cjzc\":null},{\"jxb\":\"T00191A1090030017\",\"kch\":\"A1090030\",\"kcm\":\"体育（俱乐部）-排球中级\",\"skzs\":\"2-18周双周\",\"skdd\":\"待定\",\"jsm\":\"陈文静\",\"kclb\":\"必修\",\"credit\":\".5学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"01010101010101010100\",\"weekNum\":[\"2\",\"4\",\"6\",\"8\",\"10\",\"12\",\"14\",\"16\",\"18\"],\"weekday\":\"5\",\"cStart\":\"3\",\"cTimes\":\"2\",\"cjzc\":null}],[],[]],[[{\"jxb\":\"A13191A2130350001\",\"kch\":\"A2130350\",\"kcm\":\"计算机网络\",\"skzs\":\"1-14周\",\"skdd\":\"4515\",\"jsm\":\"曹岭\",\"kclb\":\"必修\",\"credit\":\"3.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"11111111111111000000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\"],\"weekday\":\"1\",\"cStart\":\"5\",\"cTimes\":\"2\",\"cjzc\":null}],[{\"jxb\":\"A04191A1110320006\",\"kch\":\"A1110320\",\"kcm\":\"大学物理B（下）\",\"skzs\":\"1-3周,5-16周\",\"skdd\":\"3204\",\"jsm\":\"柴昭\",\"kclb\":\"必修\",\"credit\":\"3.0学分\",\"xklx\":\"重修\",\"bjlbandbjh\":null,\"week\":\"11101111111111110000\",\"weekNum\":[\"1\",\"2\",\"3\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\"],\"weekday\":\"2\",\"cStart\":\"5\",\"cTimes\":\"2\",\"cjzc\":null}],[],[],[],[],[]],[[],[{\"jxb\":\"A13191A2130060001\",\"kch\":\"A2130060\",\"kcm\":\"多媒体技术及应用\",\"skzs\":\"1-4周\",\"skdd\":\"2308\",\"jsm\":\"赵志强\",\"kclb\":\"选修\",\"credit\":\"1.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"11110000000000000000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\"],\"weekday\":\"2\",\"cStart\":\"7\",\"cTimes\":\"2\",\"cjzc\":null}],[{\"jxb\":\"SK13191A2130350001\",\"kch\":\"A2130350\",\"kcm\":\"计算机网络\",\"skzs\":\"10-16周双周\",\"skdd\":\"综合实验楼B413/B414\",\"jsm\":\"曹岭\",\"kclb\":\"必修\",\"credit\":\".0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000010101010000\",\"weekNum\":[\"10\",\"12\",\"14\",\"16\"],\"weekday\":\"3\",\"cStart\":\"7\",\"cTimes\":\"2\",\"cjzc\":null}],[{\"jxb\":\"A13191A1100040014\",\"kch\":\"A1100040\",\"kcm\":\"中国近现代史纲要\",\"skzs\":\"1-10周\",\"skdd\":\"2102\",\"jsm\":\"郭海成\",\"kclb\":\"必修\",\"credit\":\"2.0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"11111111110000000000\",\"weekNum\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\"],\"weekday\":\"4\",\"cStart\":\"7\",\"cTimes\":\"2\",\"cjzc\":null}],[],[],[]],[[],[],[{\"jxb\":\"SK13191A2130520001\",\"kch\":\"A2130520\",\"kcm\":\"人机交互技术及应用\",\"skzs\":\"9-16周\",\"skdd\":\"综合实验楼A501/A502\",\"jsm\":\"叶晓静\",\"kclb\":\"选修\",\"credit\":\".0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00000000111111110000\",\"weekNum\":[\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\"],\"weekday\":\"3\",\"cStart\":\"9\",\"cTimes\":\"4\",\"cjzc\":null},{\"jxb\":\"A13191A1100010057\",\"kch\":\"A1100010\",\"kcm\":\"形势与政策\",\"skzs\":\"5-8周\",\"skdd\":\"2310\",\"jsm\":\"白卫华\",\"kclb\":\"必修\",\"credit\":\".0学分\",\"xklx\":null,\"bjlbandbjh\":null,\"week\":\"00001111000000000000\",\"weekNum\":[\"5\",\"6\",\"7\",\"8\"],\"weekday\":\"3\",\"cStart\":\"9\",\"cTimes\":\"2\",\"cjzc\":null}],[],[],[],[]],[[],[],[],[],[],[],[]]]";
        ObjectMapper objectMapper= new ObjectMapper();
        List lists = objectMapper.readValue(ss, List.class);
        for (Object list1:lists){
            for (Object list2:(List) list1){
                for (Object keChengInfo:(List)list2){
                    System.out.println(keChengInfo);
                    System.out.println("==========================================================");
                }
            }

        }

    }


    //老咔叽拉萨扩大解放了卡就是了拉开距离






}
