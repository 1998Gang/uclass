




import cqupt.jyxxh.uclass.pojo.ClassInfo;
import cqupt.jyxxh.uclass.service.GetInfoFromWx;

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
import redis.clients.jedis.Jedis;


import javax.naming.directory.Attributes;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class test {

    /**
     * 使用LDAP验证统一身份
     */
    @Test
    public  void yige() {

        AuthenResult authenResult=UserAuth.authen("7207620","SSD");
        Attributes attrs = authenResult.getAttrs();
        HashMap<String, String> stringStringHashMap = Parse.ParseAttributes(attrs);
        System.out.println(attrs);
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
        GetInfoFromWx getInfoFromVx=new GetInfoFromWx();
        String openid = getInfoFromVx.getOpenid("033jPVCd1XtRAz0jdMAd1lLFCd1jPVCD");
        System.out.println(openid);
    }



    @Test
    public void getmassage() throws IOException {
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


    public  static boolean isbaobao(String A,String B){

        boolean flage=true;

        char[] chars = A.toCharArray();

        for (int i=0;i<chars.length;i++){
            if (B.indexOf(chars[i])==-1){
                flage=false;
            }
        }

        return flage;
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
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

        List<ClassInfo> classInfoList = null;

        //1.生成httpclient
        CloseableHttpClient httpClient= HttpClients.createDefault();
        CloseableHttpResponse response=null;
        //2.创建get请求
        HttpGet httpGet=new HttpGet("http://jwzx.cqupt.edu.cn/kebiao/kb_stu.php?xh=2017214033");
        //3.发请求
        response=httpClient.execute(httpGet);
        //4.判断响应状态码为200在继续执行
        if (response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            //5.获取响应内容
            HttpEntity httpEntity=response.getEntity();
            String html= EntityUtils.toString(httpEntity,"utf-8");
            //System.out.println(html);

            // jsoup解析
            Document doc= Jsoup.parse(html);


            Element kbStuTabslist=doc.getElementById("kbStuTabs-table");
            //获取printTable
            Elements printTables=kbStuTabslist.getElementsByClass("printTable");


            //获取table

            for(Element printTable:printTables){
                System.out.println("==================kkkk=========================");
                Elements tables = printTable.getElementsByTag("table");
                for (Element table:tables){
                    Elements tbody = table.getElementsByTag("tbody  ");
                    Elements trs = tbody.select("tr");

                    int qsjs=1;//课程开始节数
                    for (int i=0;i<trs.size();i++){
                        //第3个tr 与第6个tr是午间休息与下午休息，跳过
                        if (i==2||i==5){
                            continue;
                        }

                        //System.out.println(qsjs+"-"+(qsjs+1)+"节");
                        Element tr=trs.get(i);
                        Elements tds=tr.select("td");
                        Element td1=tds.get(0);
                        td1.attr("rowspan");


                        for (int j=1;j<tds.size();j++){
                            //System.out.println("星期"+(j)+":"+qsjs+"-"+(qsjs+1)+"节");
                            Element td=tds.get(j);
                            Elements divs=td.getElementsByClass("kbTd");
                            for (int k=0;k<divs.size();k++){

                                Element div = divs.get(k);
                                //System.out.println(div.html());
                                String html1 = div.html();

                                //System.out.println(clma);
                                ClassInfo classInfo = parseClass(html1);
                                //
                                String zc = div.attr("zc");

                                classInfo.setWeek(zc);  //上课周数
                                classInfo.setcStart(String.valueOf(qsjs));//上课  起始节数
                                classInfo.setWeekday(String.valueOf(j));//上课 天

                                System.out.println(classInfo);
                                //classInfoList.add(classInfo);

                            }
                        }
                        qsjs+=2;
                    }
                }
            }
        }
    }


    public ClassInfo parseClass(String s){
        ClassInfo classInfo=new ClassInfo();
        String[] s1 = s.split("\n");
        /*System.out.println(s1.length);
        System.out.println(Arrays.toString(s1));
        for (String ss:s1){
            System.out.println(ss);
        }*/
        classInfo.setJxb(s1[0]);//教学班号
        classInfo.setKch(s1[1].substring(s1[1].indexOf("<br>")+4,s1[1].indexOf("-")));//课程号
        classInfo.setKcm(s1[1].substring(s1[1].indexOf("-")+1));  //课程名

        if (isbaobao("综合实验楼",s1[2])){
            classInfo.setSkdd(s1[2].substring(s1[2].indexOf("综合实验楼"),s1[2].length()-2));
        }else {
            classInfo.setSkdd(s1[2].substring(s1[2].indexOf("：")+1)); //上课地点
        }

        classInfo.setJsm(s1[6].substring(s1[6].indexOf(">")+1,s1[6].indexOf("修")-2));//教师名
        classInfo.setKclb(s1[6].substring(s1[6].lastIndexOf(" ")-2,s1[6].lastIndexOf(" ")));//课程类别
        classInfo.setCredit(s1[6].substring(s1[6].lastIndexOf(" ")+1,s1[6].indexOf("</span>")));//学分
        //上课节数
        if (isbaobao("3节连上",s1[4])){
            classInfo.setcTimes("3");
        }else if(isbaobao("4节连上",s1[4])){
            classInfo.setcTimes("4");
        }else {
            classInfo.setcTimes("2");
        }

        return classInfo;
    }

    /**
     * 测试redis连接
     */
    @Test
    public void testRedis(){
        Jedis jedis=new Jedis("127.0.0.1");
        System.out.println(jedis.ping());
    }







}
