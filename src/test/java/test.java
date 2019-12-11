




import cqupt.jyxxh.uclass.pojo.KebiaoInfo;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.service.GetInfoFromWxService;

import cqupt.jyxxh.uclass.utils.Parse;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;
import cqupt.jyxxh.uclass.utils.yige.AuthenResult;
import cqupt.jyxxh.uclass.utils.yige.UserAuth;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import redis.clients.jedis.*;
import redis.clients.jedis.util.ShardInfo;


import javax.naming.directory.Attributes;
import java.io.IOException;
import java.util.*;


public class test {

    /**
     * 使用LDAP验证统一身份
     */
    @Test
    public  void yige() {

        AuthenResult authenResult=UserAuth.authen("7207619","SSD");
        Attributes attrs = authenResult.getAttrs();
        HashMap<String, String> stringStringHashMap = Parse.ParseAttributes(attrs);
        System.out.println(stringStringHashMap);
        /*HashMap<String,String> attrsMap=new HashMap<>();
        String ldapBack=attrs.toString().substring(1,attrs.toString().length()-1).replace(" ","");
        String ldapBackArry[]=ldapBack.split(",");

        for (int i=0;i<ldapBackArry.length;i++){
            String key=ldapBackArry[i].substring(0,ldapBackArry[i].indexOf("="));
            String value=ldapBackArry[i].substring(ldapBackArry[i].indexOf(":")+1);
            attrsMap.put(key,value);
        }*/

       /* System.out.println(attrsMap);*/


    }

    /**
     * 测试GetinfoFromWx
     */
    @Test
    public void ioio(){
        GetInfoFromWxService getInfoFromVx=new GetInfoFromWxService();
        String openid = getInfoFromVx.getOpenid("033jPVCd1XtRAz0jdMAd1lLFCd1jPVCD");
        System.out.println(openid);
    }

    @Test
    public void getmassage()  {
        SendHttpRquest sendHttpRquest=new SendHttpRquest();
        String stuJsonInfo=sendHttpRquest.getJsonfromhttp("http://jwzx.cqupt.edu.cn/kebiao/kb_tea.php","teaId=030403");
        System.out.println("=============教务在线返回原始json===============");
        System.out.println(stuJsonInfo);
        System.out.println("=============教务在线返回转换编码之后的json===============");
        String stuJsonInfoZW=decodeUnicode(stuJsonInfo);
        System.out.println(stuJsonInfoZW);
        /*System.out.println("=============将返回的教师json数据转换为Teacher集合===============");
        List<Teacher> teachers = Parse.ParseJsonToTeacher(stuJsonInfoZW);
        System.out.println(teachers);
        System.out.println("=============输出符合条件的教师信息===============");*/

        /*AuthenResult authenResult=UserAuth.authen("0101303","SSD");
        Attributes attrs = authenResult.getAttrs();
        System.out.println(attrs);
        HashMap<String, String> stringStringHashMap = Parse.ParseAttributes(attrs);
        String xm=stringStringHashMap.get("cn");
        String xy=stringStringHashMap.get("edupersonorgdn");
        System.out.println(xm+"===="+xy);



        for (Teacher teacher:teachers){

            if (isbaobao(xm,teacher.getTeaName())&&isbaobao(xy,teacher.getYxm())){
                System.out.println("=============输出符合条件的教师信息===============");
                System.out.println(teacher);

            }

        }*/

    }

    @Test
    public void getEduAccount(){
        SendHttpRquest sendHttpRquest=new SendHttpRquest();
        String JsonInfo = sendHttpRquest.getJsonfromhttp("http://jwzx.cqupt.edu.cn/data/json_TeacherSearch.php","searchKey="+"向敏");
        //4.1.3 将请求回的json数据转码


       /* Student student = Parse.ParseJsonToStudent(JsonInfo);
        System.out.println(student);*/


        //4.1.4 将josn数据解析为Teacher对象
        List<Teacher> teachers = Parse.ParseJsonToTeacher(JsonInfo);
        for (Teacher teacher:teachers){
            //判断教师姓名，与教师所属学院，同时符合的为正确的教师
            if (Parse.isbaohan("经管", teacher.getYxm())){
                System.out.println(teacher);
            }
        }




    }


    public  static boolean isbaobao(String A,String B){

        boolean flage=true;

        char[] chars = A.toCharArray();

        for (char aChar : chars) {
            if (B.indexOf(aChar) == -1) {
                flage = false;
            }
        }

        return flage;
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }


    @Test
    public  void kjkj(){
        boolean isbaobao = isbaobao("软件学院", "件工程/离开家离开家学院");
        System.out.println(isbaobao);
    }


    @Test
    public void client() throws IOException {

        List<KebiaoInfo> stuKebiaoInfoList = null;

        //1.生成httpclient
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response;
        //2.创建get请求
        HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/kebiao/kb_stu.php?xh=2017214032");
        //HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/kebiao/kb_tea.php?teaId=030512");
        //3.发请求
        response=httpClient.execute(httpGet);
        //4.判断响应状态码为200在继续执行
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            //5.获取响应内容
            HttpEntity httpEntity=response.getEntity();
            String html= EntityUtils.toString(httpEntity,"utf-8");


            // jsoup解析
            Document doc= Jsoup.parse(html);

            Element kbStuTabslist=doc.getElementById("kbStuTabs-table");
            //Element kbStuTabslist=doc.getElementById("kbTeaTabs-table");
            //获取printTable
            Elements printTables=kbStuTabslist.getElementsByClass("printTable");


            //获取table

            for(Element printTable:printTables){
                System.out.println("==================kkkk=========================");
                /*Elements tables = printTable.getElementsByTag("table");
                for (Element table:tables){*/
                    Elements tbody = printTable.getElementsByTag("tbody  ");
                    Elements trs = tbody.select("tr");

                    int qsjs=1;//课程开始节数
                    for (int i=0;i<trs.size();i++){
                        //第3个tr 与第6个tr是午间休息与下午休息，跳过
                        if (i==2||i==5){
                            continue;
                        }

                        Element tr=trs.get(i);
                        Elements tds=tr.select("td");

                        for (int j=1;j<tds.size();j++){
                            System.out.println("星期"+(j)+":"+qsjs+"-"+(qsjs+1)+"节");
                            Element td=tds.get(j);
                            Elements divs=td.getElementsByClass("kbTd");
                            for (Element div : divs) {

                                //System.out.println(div.html());
                                String html1 = div.html();

                                //System.out.println(clma);
                                KebiaoInfo stuKebiaoInfo = parseClass(html1);
                                //
                                String zc = div.attr("zc");

                                stuKebiaoInfo.setWeek(zc);  //上课周数
                                stuKebiaoInfo.setcStart(String.valueOf(qsjs));//上课  起始节数
                                stuKebiaoInfo.setWeekday(String.valueOf(j));//上课 天

                                System.out.println(stuKebiaoInfo);
                                //stuKebiaoInfoList.add(stuKebiaoInfo);

                            }
                        }
                        qsjs+=2;
                    }
                //}
            }
        }
    }


    public KebiaoInfo parseClass(String s){
        KebiaoInfo stuKebiaoInfo =new KebiaoInfo();
        String[] s1 = s.split("\n");
        System.out.println(s1.length);
        /*System.out.println(Arrays.toString(s1));
        for (String ss:s1){
            System.out.println(ss);
        }*/
        stuKebiaoInfo.setJxb(s1[0]);//教学班号
        stuKebiaoInfo.setKch(s1[1].substring(s1[1].indexOf("<br>")+4,s1[1].indexOf("-")));//课程号
        stuKebiaoInfo.setKcm(s1[1].substring(s1[1].indexOf("-")+1));  //课程名

        if (isbaobao("综合实验楼",s1[2])){
            stuKebiaoInfo.setSkdd(s1[2].substring(s1[2].indexOf("综合实验楼"),s1[2].length()-2));
        }else {
            stuKebiaoInfo.setSkdd(s1[2].substring(s1[2].indexOf("：")+1)); //上课地点
        }

        stuKebiaoInfo.setJsm(s1[6].substring(s1[6].indexOf(">")+1,s1[6].indexOf("修")-2));//教师名
        stuKebiaoInfo.setKclb(s1[6].substring(s1[6].lastIndexOf(" ")-2,s1[6].lastIndexOf(" ")));//课程类别
        stuKebiaoInfo.setCredit(s1[6].substring(s1[6].lastIndexOf(" ")+1,s1[6].indexOf("</span>")));//学分
        //上课节数
        if (isbaobao("3节连上",s1[4])){
            stuKebiaoInfo.setcTimes("3");
        }else if(isbaobao("4节连上",s1[4])){
            stuKebiaoInfo.setcTimes("4");
        }else {
            stuKebiaoInfo.setcTimes("2");
        }

        return stuKebiaoInfo;
    }

    /**
     * 测试redis连接
     */
    @Test
    public void testRedis(){
        Jedis jedis=new Jedis("118.25.64.213",6379);

        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
    }

    @Test
    public void testJedisPool(){
        //1.创建jedisPoolConfig连接池对象
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        //2.设置最大连接数、最大空闲连接等
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        //3.创建连接池对象
        JedisPool jedisPool=new JedisPool("118.25.64.213",6379);
        Jedis jedis = jedisPool.getResource();
        jedis.set("jedispool","123");
        String jedispool = jedis.get("jedispool");
        System.out.println(jedispool);
    }

    @Test
    public void testShareJedisPool(){
        //1.创建jedisPoolConfig连接池对象
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        //2.设置最大连接数、最大空闲连接等
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        //3.定义redis集群节点,本例设置了两个reids连接节点
        List<JedisShardInfo> jedisShardInfos=new ArrayList<>();

        JedisShardInfo jedisShardInfo1=new JedisShardInfo("118.25.64.213",6379);
        jedisShardInfo1.setPassword("MtBv2omyQfTHYQpb1yLX");
        jedisShardInfos.add(jedisShardInfo1);

        JedisShardInfo jedisShardInfo2 = new JedisShardInfo("127.0.0.1", 6379);
        jedisShardInfo2.setPassword("root");
        jedisShardInfos.add(jedisShardInfo2);
        //3.创建reids集群连接池对象
        ShardedJedisPool shardedJedisPool=new ShardedJedisPool(jedisPoolConfig,jedisShardInfos);
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        //创建50条数据
        for (int i=1;i<50;i++){
            System.out.println(shardedJedis.get("shardedjedispool-"+i));

            //shardedJedis.set("shardedjedispool-"+i,""+i);
        }


    }



    @Test
    public void ttttt(){
        String kk="1234".substring(0,2);
        System.out.println(kk);
    }




}
