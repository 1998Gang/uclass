




import cqupt.jyxxh.uclass.pojo.KeChengInfo;
import cqupt.jyxxh.uclass.pojo.Teacher;
import cqupt.jyxxh.uclass.utils.GetDataFromWX;

import cqupt.jyxxh.uclass.utils.Parse;
import cqupt.jyxxh.uclass.utils.SendHttpRquest;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import redis.clients.jedis.*;


import javax.naming.directory.Attributes;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;


public class test {



    /**
     * 测试GetinfoFromWx
     */
    @Test
    public void getinfoFromWx(){
        GetDataFromWX getInfoFromVx=new GetDataFromWX();
        String openid = getInfoFromVx.getOpenid("033jPVCd1XtRAz0jdMAd1lLFCd1jPVCD");
        System.out.println(openid);
    }

    @Test
    public void getJsonfromhttp()  {

        SendHttpRquest sendHttpRquest=new SendHttpRquest();
        String stuJsonInfo=sendHttpRquest.getJson("http://jwzx.cqupt.edu.cn/kebiao/kb_tea.php","teaId=030403");


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
    public  void kjkj() throws UnsupportedEncodingException {
        boolean isbaobao = isbaobao("软件学院", "件工程/离开家离开家学院");
        System.out.println(isbaobao);


        String s="你好 hello";
        System.out.println(Parse.getEncoding(s));

        byte[] b=s.getBytes("GBK");
        System.out.println(b.toString());

        String s1=new String(b,"GBK");
        System.out.println(Parse.getEncoding(s1));
        System.out.println(s1);
    }


    /**
     * 课表相关操作
     * @throws IOException
     */
    @Test
    public void kebiao() throws IOException {

        List<KeChengInfo> stuKeChengInfoList = null;

        //1.生成httpclient
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response;
        //2.创建get请求
        HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/kebiao/kb_stu.php?xh=2017214033");
        //HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/kebiao/kb_tea.php?teaId=130702");
        //HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/kebiao/kb_tea.php?teaId=130712");
        //        //3.发请求
        response=httpClient.execute(httpGet);
        //4.判断响应状态码为200在继续执行
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            //5.获取响应内容
            HttpEntity httpEntity=response.getEntity();
            String html= EntityUtils.toString(httpEntity,"utf-8");


            ArrayList<ArrayList<ArrayList<KeChengInfo>>> jj = null;  //课表最外层，代表节数
            ArrayList<ArrayList<KeChengInfo>> xx;             //课表第二层，代表上课的星期数
            ArrayList<KeChengInfo> jandx;                     //课表第三层，代表节数，星期数相同的时间里的课程。

            // jsoup解析html页面
            Document doc= Jsoup.parse(html);


            //获取stuPanl
            Element stuPanel=doc.getElementById("stuPanel");
            //获取tbody（教务在线学生课表的 表格）,实际tbody只有一个
            Elements tbodys = stuPanel.getElementsByTag("tbody");

            /*//获取teaPanel
            Element teaPanel = doc.getElementById("teaPanel");
            //获取tbody
            Elements tbodys = teaPanel.getElementsByTag("tbody");*/



                 Element tbody=tbodys.get(0);
                //解析表格 <tr>代表上课的节数,一共8个。第3个<tr>代表午间休息，第6个<tr>代表下午大课间,无用去掉。   <td>标签代表上课的星期数
                Elements trs= tbody.select("tr");
                trs.remove(2);
                trs.remove(4);
                //遍历<tr>标签，一共6个，“i”是标记参数（上课节数）。 0：12节、1：34节、2：56节.......5：11 12节
                int j=-1;


                //最外层 节数
                jj=new ArrayList<>();
                for (Element tr:trs){
                    j++;
                    //根据<tr>获取<td>带代表上课星期数，一共8个，第一个无用去掉。
                    Elements tds = tr.select("td");
                    tds.remove(0);
                    //遍历<td>标签，一共7个。代表星期一到星期天。"x"是标记参数，0：星期一、1：星期二.......6：星期天
                    int x=-1;

                    //星期数
                    xx=new ArrayList<>();
                    for (Element td:tds){
                        x++;
                        //此时已经定位到了星期几，第几节课了。
                        System.out.print("星期"+(x+1)+":"+(j*2+1)+"节");
                        //相同星期，相同节数可能有多节课（上课周数不同），多个<div class="KbTd">。
                        Elements kbTds = td.getElementsByClass("kbTd");

                        //创建集合(最里层)
                        jandx=new ArrayList<>();
                        for (int k=0;k<kbTds.size();k++){
                            //此时定位到具体的课程
                            Element kbTd = kbTds.get(k);
                            //获取上课周数,"10101010101010101000"，一共20位，代表20周，为1带表有课，0为没课。
                            String zc = kbTd.attr("zc");
                            //解析上课周数，将这20位的数字转位具体的上课周数的集合
                            List<String> strings = Parse.parseZCtoWeekNum(zc);
                            //解析具体的课程信息
                           /* KeChengInfo keChengInfo = parseKebiaoTostu(kbTd.html());*/
                            //教师
                           /* KeChengInfo keChengInfo = parseTokebiaoinfo(kbTd.html(),"t");*/
                            //学生
                            KeChengInfo keChengInfo = parseTokebiaoinfo(kbTd.html(),"s");

                            //添加一些其他数据
                            keChengInfo.setWeekNum(strings);
                            keChengInfo.setWeek(zc);
                            keChengInfo.setcStart(String.valueOf(j*2+1));
                            keChengInfo.setWeekday(String.valueOf(x+1));


                            System.out.print(keChengInfo);
                            //放入集合
                            jandx.add(keChengInfo);

                        }
                        xx.add(jandx);

                        System.out.print("  ||  ");
                    }
                    jj.add(xx);
                    System.out.println();
                }


        }
    }

    private KeChengInfo parseTokebiaoinfo(String kebiaoHtml, String type){
        KeChengInfo keChengInfo=new KeChengInfo();
        String[] s1=kebiaoHtml.split("\n");
        /*for (String s:s1){
            System.out.println(s);
        }*/

        //教学班
        keChengInfo.setJxb(s1[0]);
        //课程号
        keChengInfo.setKch(s1[1].substring(s1[1].indexOf("<br>")+4,s1[1].indexOf("-")));
        //课程名
        keChengInfo.setKcm(s1[1].substring(s1[1].indexOf("-")+1));
        //上课地点
        if (isbaobao("综合实验楼",s1[2])){
            keChengInfo.setSkdd(s1[2].substring(s1[2].indexOf("综合实验楼"),s1[2].length()-2));
        }else {
            keChengInfo.setSkdd(s1[2].substring(s1[2].indexOf("：")+1,s1[2].length()-1));
        }
        //上课周数（如：1周,4-8周,10-18周 这样的字符串）
        keChengInfo.setSkzs(s1[3].substring(s1[3].indexOf("<br>")+4));
        //上课节数
        if (isbaobao("3节连上",s1[4])){
            keChengInfo.setcTimes("3");
        }else if(isbaobao("4节连上",s1[4])){
            keChengInfo.setcTimes("4");
        }else {
            keChengInfo.setcTimes("2");
        }
        //教师名
        keChengInfo.setJsm(s1[6].substring(s1[6].indexOf(">")+1,s1[6].indexOf("修")-2));
        //课程类别
        keChengInfo.setKclb(s1[6].substring((s1[6].indexOf("修")-1),s1[6].indexOf("修")+1));
        //学分，如果是学生进行该步骤
        if ("s".equals(type)){
            keChengInfo.setCredit(s1[6].substring(s1[6].lastIndexOf(" ")+1,s1[6].indexOf("</span>")));
            //上课状态，如  自修 重修 再修等
            if (s1.length==11){
                keChengInfo.setSklx(s1[8].substring(s1[8].indexOf("\">")+2,s1[8].indexOf("</")));
            }

        }
        //上课班级类别以及班级号，如果是老师进行该步骤
        if ("t".equals(type)){
            List<String> bjlbandbjh=new ArrayList<>();
            for (int i=7;i<s1.length-2;i++){
                bjlbandbjh.add(s1[i].substring(s1[i].indexOf("<br>")+4));
            }
            keChengInfo.setBjlbandbjh(bjlbandbjh);
        }

        return keChengInfo;
    }






    /**
     * 测试redis连接
     */
    @Test
    public void testRedis(){
        Jedis jedis=new Jedis("118.25.64.213",6379);
        jedis.auth("MtBv2omyQfTHYQpb1yLX");

        jedis.select(1);
        /*for (int i=1;i<100;i++){
            jedis.set("keyss"+i,String.valueOf(i));
        }*/


        Set<String> keys = jedis.keys("key*");
        System.out.println(keys);

        for (String s:keys){
            System.out.println(jedis.get(s));
        }

    }

    /**
     * 测试jedis连接池
     */
    @Test
    public void testJedisPool(){
        //1.创建jedisPoolConfig连接池对象
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        //2.设置最大连接数、最大空闲连接等
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        //3.创建连接池对象
        JedisPool jedisPool=new JedisPool(jedisPoolConfig,"118.25.64.213",6379);
        Jedis jedis = jedisPool.getResource();
        jedis.set("jedispool","123");
        String jedispool = jedis.get("jedispool");
        System.out.println(jedispool);
    }

    /**
     * 测试jedis集群
     */
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
    public void getJsonFromHttpTest() throws IOException {
        URL url=new URL("http://jwzx.cqupt.edu.cn/data/json_teacherSearch.php?searchKey=向敏");

        Document parse = Jsoup.parse(url,1000000);
        String body = parse.body().text();
        String s = Parse.decodeUnicode(body);
        System.out.println(s);

    }



    @Test
    public void testArry(){
        String s="1010101010";
        char[] chars = s.toCharArray();
        for (int i=0;i<chars.length;i++){
            if (chars[i]=='1'){
                System.out.println(i+1);
            }

        }

    }




    @Test
    public void tes(){



    }


    @Test
    public void ee()  {

        String ok="2019-2020学年1学期 第 16 周 星期 1 2019年12月16日";
        int indexOf = ok.indexOf("年",13);
        String ok1=ok.substring(indexOf,indexOf+3);
        System.out.println(indexOf);
        System.out.println(ok1);



    }


    /**
     * 获取校历时间
     */
    @Test
    public void getSTime(){
        try {
            String htmlFromHttp = SendHttpRquest.getHtml("http://jwzx.cqupt.edu.cn/");
            Map<String, String> map = Parse.parseHtmlToSchoolTime(htmlFromHttp);

            System.out.println(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void loginJWZX() throws IOException {
        //创建一个httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 1.访问统一认证登陆网页,获取后续post登陆验证所需参数
        String cookie1=null;
        HttpGet httpGet_authserver_login=new HttpGet("https://ids.cqupt.edu.cn/authserver/login?service=http%3A%2F%2Fjwzx.cqupt.edu.cn%2Ftysfrz%2Findex.php");
        CloseableHttpResponse authserver_login = httpClient.execute(httpGet_authserver_login);
        // 获取cookie
        Header[] cookies = authserver_login.getHeaders("Set-Cookie");
        for (Header header:cookies){
            HeaderElement[] elements = header.getElements();
            System.out.println(elements.length);
            for (HeaderElement headerElement:elements){

                String name = headerElement.getName();
                String value = headerElement.getValue();
                System.out.println(name+value);
                cookie1=name+"="+value;
            }
        }
        //获取It\execution\_eventId\rmShown,这四个参数数据将在验证统一身份时，作为表单参数一起提交。
        Map<String,String> form=new HashMap<>();

        HttpEntity authserver_loginEntity = authserver_login.getEntity();
        String s = EntityUtils.toString(authserver_loginEntity);
        Document parse = Jsoup.parse(s);
        Elements input = parse.getElementsByTag("input");
        for (Element element:input){
            String name = element.attr("name");
            String value = element.attr("value");

            if ("lt".equals(name)||"execution".equals(name)||"_eventId".equals(name)||"rmShown".equals(name)){
                form.put(name,value);
            }

        }


        System.out.println("========第一次请求统一身份验证=========："+cookie1);


        // 2.再次访问，这一次使用post，同时将第一次访问获取的cookie添加在请求头里面，同时添加统一认证身份

        //开始之前定义一个字符串，用来存post请求后，身份验证成功重定向的地址。
        String location=null;
        List<String> SetCookie=new ArrayList<>();

        HttpPost httpPost_authserver_login=new HttpPost("https://ids.cqupt.edu.cn/authserver/login?service=http%3A%2F%2Fjwzx.cqupt.edu.cn%2Ftysfrz%2Findex.php");
        // 2.1 设置参数
        List<NameValuePair> basicNameValuePairList=new ArrayList<>();
        basicNameValuePairList.add(new BasicNameValuePair("username","1655728"));
        basicNameValuePairList.add(new BasicNameValuePair("password","98LD99LP"));
        basicNameValuePairList.add(new BasicNameValuePair("lt",form.get("lt")));
        basicNameValuePairList.add(new BasicNameValuePair("execution",form.get("execution")));
        basicNameValuePairList.add(new BasicNameValuePair("_eventId",form.get("_eventId")));
        basicNameValuePairList.add(new BasicNameValuePair("rmShown",form.get("rmShown")));
        // 2.2创建from表单实体
        UrlEncodedFormEntity urlEncodedFormEntity=new UrlEncodedFormEntity(basicNameValuePairList,"utf-8");
        // 2.3将from表单添加到post请求里
        httpPost_authserver_login.setEntity(urlEncodedFormEntity);
        // 2.4 设置相应头，cookie
        httpPost_authserver_login.setHeader("Cookie",cookie1);
        // 2.5 发起请求
        CloseableHttpResponse execute = httpClient.execute(httpPost_authserver_login);
        // 2.6 判断响应状态码，如果为302说明身份验证成功，进行跳转了，如果为200说明身份验证失败。
        int statusCode = execute.getStatusLine().getStatusCode();
        System.out.println("==========身份验证url状态码============="+statusCode);
        if (statusCode==HttpStatus.SC_MOVED_TEMPORARILY){
            //身份验证成功，获取重定向的url地址。响应头名称为location
            Header[] locations = execute.getHeaders("Location");
            for (Header header:locations){
                //获取Location中的元素
                HeaderElement[] elements = header.getElements();
                System.out.println(elements.length);
                for (HeaderElement headerElement:elements){
                    String name = headerElement.getName();
                    String value = headerElement.getValue();
                    location=name+"="+value;
                }
            }
            //获取Sercookie
            Header[] setcookie = execute.getHeaders("Set-Cookie");
            for(Header header:setcookie){
                SetCookie.add(header.getName()+"="+header.getValue());
            }
        }
        System.out.println("========身份验证成功获取的location=========："+location);



        // 3.上一步获取的重定向地址
        HttpGet httpGet_ticket=new HttpGet(location);


        CloseableHttpResponse ticket = httpClient.execute(httpGet_ticket);
        /*int statusCode_ticket = ticket.getStatusLine().getStatusCode();
        System.out.println("=========ticket=========："+statusCode_ticket);
        Header[] allHeaders11 = ticket.getAllHeaders();
        for (Header header:allHeaders11){
            System.out.print(header.getName()+":");
            HeaderElement[] elements = header.getElements();
            for (HeaderElement element:elements){
                System.out.print(element+" ");
            }
            System.out.println();
        }*/





        CloseableHttpClient build = HttpClients.custom().build();
        HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/student/skjh.php");
        assert location != null;
        httpGet.setHeader("Cookie","PHPSESSID"+location.substring(location.indexOf("=")));
        CloseableHttpResponse skjh = build.execute(httpGet);
        skjh.getStatusLine().getStatusCode();
        HttpEntity entity = skjh.getEntity();
        String skjh_html = EntityUtils.toString(entity, "utf-8");

        //循环打印输出
        Map<String, String> map = Parse.parseHtmlToCJZC(skjh_html);
        Set<String> strings = map.keySet();
        for (String ss:strings){
            String s1 = map.get(ss);

            System.out.println(s1);
        }
        System.out.println(map);





    }

}
