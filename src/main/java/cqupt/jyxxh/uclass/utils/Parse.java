package cqupt.jyxxh.uclass.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 【工具类】
 * 用于解析相关数据的工具类
 * 解析工具类，所有方法均为静态方法。
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 15:53 2019/11/5
 */
public class Parse {

    private final static Logger logger = LoggerFactory.getLogger(Parse.class);  //日志


    /**
     * 转化编码方式   将经过URLecode编码 转换回来
     * 例如：
     * 转化之前：{"code":0,"info":"ok","returnData":[{"xh":"2017214033","xm":"\u5f6d\u6e1d\u521a","xmEn":"Peng Yu Gang ","xb":"\u7537","bj":"13001701","zyh":"1300","zym":"\u8f6f\u4ef6\u5de5\u7a0b","yxh":"13","yxm":"\u8f6f\u4ef6\u5de5\u7a0b\u5b66\u9662","nj":"2017","csrq":"19981030","xjzt":"\u5728\u6821","rxrq":"201709","yxmen":"School of Software","zymEn":"Software Engineering","xz":4,"mz":"\u6c49\u65cf                "}]}
     * 转化之后：{"code":0,"info":"ok","returnData":[{"xh":"2017214033","xm":"彭渝刚","xmEn":"Peng Yu Gang ","xb":"男","bj":"13001701","zyh":"1300","zym":"软件工程","yxh":"13","yxm":"软件工程学院","nj":"2017","csrq":"19981030","xjzt":"在校","rxrq":"201709","yxmen":"School of Software","zymEn":"Software Engineering","xz":4,"mz":"汉族                "}]}
     *
     * @param theString 未转码之前的字符串
     * @return 转码之后的字符串
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);
        for (int x = 0; x < len; ) {
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


    /**
     * 解析学生接口返回的json数据 json对象转为Student对象
     *
     * @param stuJsonInfo ：学生信息json数据,例如:{"code":0,"info":"ok","returnData":[{"xh":"2017214033","xm":"\u5f6d\u6e1d\u521a","xmEn":"Peng Yu Gang ","xb":"\u7537","bj":"13001701","zyh":"1300","zym":"\u8f6f\u4ef6\u5de5\u7a0b","yxh":"13","yxm":"\u8f6f\u4ef6\u5de5\u7a0b\u5b66\u9662","nj":"2017","csrq":"19981030","xjzt":"\u5728\u6821","rxrq":"201709","yxmen":"School of Software","zymEn":"Software Engineering","xz":4,"mz":"\u6c49\u65cf                "}]}
     * @return 返回学生信息
     */
    public static Student ParseJsonToStudent(String stuJsonInfo) {
        Student studentInfo = new Student();
        //判断传入参数是否为空
        if ("".equals(stuJsonInfo) || stuJsonInfo == null) {
            logger.debug("【解析学生json数据】失败！stuJsonInfo为空");
            return studentInfo;
        }

        try {
            //创建jackson核心对象Obujectmapper
            ObjectMapper objectMapper = new ObjectMapper();
            //读取json字符串获取顶层的jsonNode对象
            JsonNode jsonNodeTop = objectMapper.readTree(stuJsonInfo);
            //获取returnData的jsonNode
            JsonNode jsonNodeReturnData = jsonNodeTop.get("returnData");
            //取出returnData对象数组中的json对象数据
            //因为学生数据是根据学号查询的，返回的json数据中 returnData数组中也只会有一个对象数据，所以数组索引为0直接读取
            JsonNode stuInfo = jsonNodeReturnData.get(0);
            studentInfo.setXm(stuInfo.get("xm").toString().replace("\"", "")); //姓名
            studentInfo.setXb(stuInfo.get("xb").toString().replace("\"", "")); //性别
            studentInfo.setXh(stuInfo.get("xh").toString().replace("\"", "")); //学号
            studentInfo.setYxm(stuInfo.get("yxm").toString().replace("\"", ""));  //学院名（院系名）
            studentInfo.setZym(stuInfo.get("zym").toString().replace("\"", ""));  //专业名
            studentInfo.setNj(stuInfo.get("nj").toString().replace("\"", ""));    //年级
            studentInfo.setBj(stuInfo.get("bj").toString().replace("\"", ""));    //班级
            studentInfo.setXjzt(stuInfo.get("xjzt").toString().replace("\"", ""));  //学籍状态
            studentInfo.setMz(stuInfo.get("mz").toString().replace("\"", ""));      //民族
            studentInfo.setCsrq(stuInfo.get("csrq").toString().replace("\"", ""));  //出生日期

        } catch (IOException e) {
            e.printStackTrace();
        }

        return studentInfo;
    }


    /**
     * 解析访问教师信息接口返回的json字符串为teacher对象
     *
     * @param teaJsonInfo 教师信息的json数据，例如：{"code":0,"info":"ok","returnData":[{"teaId":"030403","teaName":"\u5411\u654f\uff08\u7ecf\uff09","xb":"\u5973","jysm":"\u5e94\u7528\u7ecf\u6d4e\u5b66\u7cfb","yxm":"\u7ecf\u6d4e\u7ba1\u7406\u5b66\u9662","zc":"\u6559\u6388"},
     *                    {"teaId":"080213","teaName":"\u5411\u654f\uff08\u81ea\u52a8\u5316\uff09","xb":"\u7537","jysm":"\u81ea\u52a8\u5316\u4e0e\u673a\u5668\u4eba\u5de5\u7a0b\u7cfb","yxm":"\u81ea\u52a8\u5316\u5b66\u9662","zc":"\u6559\u6388"}]}
     * @return 返回教师信息list集合
     */
    public static List<Teacher> ParseJsonToTeacher(String teaJsonInfo) {
        //创建教师集合
        List<Teacher> teaList = new ArrayList<>();
        //判断传入参数是否为空
        if (null == teaJsonInfo || "".equals(teaJsonInfo)) {
            logger.debug("【解析教师json格式信息（Parse.ParseJsonToTeacher）】 解析失败！参数teaJsonInfo为空");
            return teaList;
        }
        try {

            //创建jackson核心对象Obujectmapper
            ObjectMapper objectMapper = new ObjectMapper();
            //读取json字符串获取顶层的jsonNode对象
            JsonNode jsonNodeTop = objectMapper.readTree(teaJsonInfo);
            //获取returnData的jsonNode
            JsonNode jsonNodeReturnData = jsonNodeTop.get("returnData");
            //循环遍历json对象数组的对象
            /*因为教师信息是根据姓名查询的，存在重名的教师，会查出多条数据，返回的json数据 reternData数组里面有多名老师信息，所以需要循环读取到教师集合里面
             * {"code":0,"info":"ok","returnData":[{"teaId":"030403","teaName":"向敏（经）","xb":"女","jysm":"应用经济学系","yxm":"经济管理学院","zc":"教授"},
             *                                     {"teaId":"080213","teaName":"向敏（自动化）","xb":"男","jysm":"自动化与机器人工程系","yxm":"自动化学院","zc":"教授"}]}
             */
            for (int i = 0; i < jsonNodeReturnData.size(); i++) {
                Teacher teacher = new Teacher();
                JsonNode teaInfo = jsonNodeReturnData.get(i);
                teacher.setTeaName(teaInfo.get("teaName").toString().replace("\"", ""));//姓名
                teacher.setTeaId(teaInfo.get("teaId").toString().replace("\"", ""));//教师号
                teacher.setXb(teaInfo.get("xb").toString().replace("\"", ""));//性别
                teacher.setJysm(teaInfo.get("jysm").toString().replace("\"", ""));//教研室
                teacher.setYxm(teaInfo.get("yxm").toString().replace("\"", ""));//学院
                teacher.setZc(teaInfo.get("zc").toString().replace("\"", ""));//职称
                teaList.add(teacher);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return teaList;
    }


    /**
     * 判断b字符串是否包含a字符串中全部字符
     *
     * @param a 被包含字符串
     * @param b 包含字符串
     * @return 返回boolean值
     */
    public static boolean isbaohan(String a, String b) {
        boolean flage = true;
        //将字符串a转换为char数组
        char[] chars = a.toCharArray();
        //循环判断chars中字符是否包含在字符串内
        for (char aChar : chars) {
            if (b.indexOf(aChar) == -1) {
                //如果有chars中字符在字符串b中没有，改flage为false
                flage = false;
            }
        }
        return flage;
    }


    /**
     * 解析教务在线获取的课表html页
     * <p>
     * 最外层集合代表上课节数，一共装6个。
     * 第二层集合代表具体节数下的上课的星期数（星期一、.....星期天），
     * 第三层集合代表具体节数、具体星期数的具体课程。（因为存在冲突课程以及单双周之类的）
     * <p>
     * 上课的周数在 KeChengInfo 里（多少周有课）
     *
     * @param html  html字符串
     * @param type  用户类型，”s“学生，"t"老师。必填
     * @param cjzcs map集合，装的成绩组成。  如：{SK13191A2130610002=[实验实践]考勤占30%，课堂表现占30%, A13191A1100040014=[理论]期末考试50%+网络学习30%+课程论文20%}
     * @return ArrayList<ArrayList < ArrayList < KeChengInfo>>> 嵌套的集合，最里层放的课程信息
     */
    public static ArrayList<ArrayList<ArrayList<KeChengInfo>>> parseHtmlToKebiaoInfo(String html, String type, Map<String, String> cjzcs) {


        //解析后的数据以嵌套集合的方式返回，定义三层集合
        ArrayList<ArrayList<ArrayList<KeChengInfo>>> jj = new ArrayList<>();//最外层，代表整个课表，装具体的上课节数（12节、34节 、....11 12节）
        ArrayList<ArrayList<KeChengInfo>> xx;                //第二层，代表具体的上课节数，装具体节数的星期数（星期一....星期天）
        ArrayList<KeChengInfo> jandx;                        //第三层，代笔具体工作日具体上课时间段对应的课程


        // 1.jsoup解析html页面
        Document doc = Jsoup.parse(html);

        // 2.获取tbody（教务在线学生课表的 表格）,实际tbody只有一个
        Elements tbodys = null;
        switch (type) {
            case "s": {
                Element stuPanel = doc.getElementById("stuPanel");
                //获取tbody（教务在线学生课表的 表格）,实际tbody只有一个
                tbodys = stuPanel.getElementsByTag("tbody");
                break;
            }
            case "t": {
                Element teaPanel = doc.getElementById("teaPanel");
                //获取tbody
                tbodys = teaPanel.getElementsByTag("tbody");
                break;
            }
        }


        // 3.解析课表表格,因为课表只有一个body（第一个），所有这里直接用索引0来获取body。
        assert tbodys != null;
        Element tbody = tbodys.get(0);
        //解析表格 <tr>代表上课的节数,一共8个。第3个<tr>代表午间休息，第6个<tr>代表下午大课间,无用去掉。   <td>标签代表上课的星期数
        Elements trs = tbody.select("tr");
        trs.remove(2);
        trs.remove(4);
        //遍历<tr>标签，一共6个，“i”是标记参数（上课节数）。 0：12节、1：34节、2：56节.......5：11 12节
        int j = -1;


        for (Element tr : trs) {
            j++;
            //根据<tr>获取<td>带代表上课星期数，一共8个，第一个无用去掉。
            Elements tds = tr.select("td");
            tds.remove(0);
            //遍历<td>标签，一共7个。代表星期一到星期天。"x"是标记参数，0：星期一、1：星期二.......6：星期天
            int x = -1;
            //此处实例化第二成集合，代表具体的上课节数，装这个上课节数下，的星期数。
            xx = new ArrayList<>();


            for (Element td : tds) {
                x++;
                //此时已经定位到了星期几，第几节课了。
                //相同星期，相同节数可能有多节课（上课周数不同），多个<div class="KbTd">。
                Elements kbTds = td.getElementsByClass("kbTd");
                //实例化第三层集合，代表具体上课节数、星期数，装这个时段的课程。
                jandx = new ArrayList<>();


                for (Element kbTd : kbTds) {
                    //此时定位到具体的课程
                    //获取上课周数,"10101010101010101000"，一共20位，代表20周，为1带表有课，0为没课。
                    String zc = kbTd.attr("zc");
                    //解析，将"10101010101010101000" to {1，3，5，7，9，11，13，15，17}
                    List<String> weekNum = Parse.parseZCtoWeekNum(zc);
                    //解析具体的课程信息，将html页面上的信息解析为 KeChengInfo。学生与老师的html页面不一样，不同的解析方式。
                    KeChengInfo keChengInfo = parseKebiaoToKeChengInfo(kbTd.html(), type, cjzcs);
                    //补充数据
                    keChengInfo.setWeek(zc); //上课周数
                    keChengInfo.setWeekNum(weekNum);//上课周数（具体数字）
                    keChengInfo.setcStart(String.valueOf(j * 2 + 1)); //上课开始节数（12节的课，该数据为 1）
                    keChengInfo.setWork_day(String.valueOf(x + 1));   //上课的工作日（星期一的课，该数据为 1）

                    //放入集合
                    jandx.add(keChengInfo);
                }

                //将具体课程集合放入代表星期数的集合
                xx.add(jandx);

            }

            //将具体星期的课程放入具体节数里面
            jj.add(xx);
        }


        return jj;
    }

    /**
     * 解析教务在线上html上的课程的具体信息(),
     *
     * @param kebiaoHtml 课程详细信息的html数据(具体的 某一节的信息。)
     * @param type       操作的用户类型 “s” 是学生，"t"是老师
     * @param cjzcs      map集合，装的是成绩组成。key是教学班，value是成绩组成描述的字符串。如：{SK13191A2130610002=[实验实践]考勤占30%，课堂表现占30%, A13191A1100040014=[理论]期末考试50%+网络学习30%+课程论文20%}
     * @return kechengInfo 课程的详细信息
     */
    private static KeChengInfo parseKebiaoToKeChengInfo(String kebiaoHtml, String type, Map cjzcs) {
        KeChengInfo keChengInfo = new KeChengInfo();
        String[] s1 = kebiaoHtml.split("\n");

        //教学班
        keChengInfo.setJxb(s1[0]);
        //课程号
        keChengInfo.setKch(s1[1].substring(s1[1].indexOf("<br>") + 4, s1[1].indexOf("-")));
        //课程名
        keChengInfo.setKcm(s1[1].substring(s1[1].indexOf("-") + 1));
        //上课地点
        if (isbaohan("综合实验楼", s1[2])) {
            keChengInfo.setSkdd(s1[2].substring(s1[2].indexOf("综合实验楼"), s1[2].length() - 2));
        } else {
            keChengInfo.setSkdd(s1[2].substring(s1[2].indexOf("：") + 1, s1[2].length() - 1));
        }
        //上课周数（如：1周,4-8周,10-18周 这样的字符串）
        keChengInfo.setSkzs(s1[3].substring(s1[3].indexOf("<br>") + 4));
        //上课节数
        if (isbaohan("3节连上", s1[4])) {
            keChengInfo.setcTimes("3");
        } else if (isbaohan("4节连上", s1[4])) {
            keChengInfo.setcTimes("4");
        } else {
            keChengInfo.setcTimes("2");
        }
        //教师名
        keChengInfo.setJsm(s1[6].substring(s1[6].indexOf(">") + 1, s1[6].indexOf("修") - 2));
        //课程类别
        keChengInfo.setKclb(s1[6].substring((s1[6].indexOf("修") - 1), s1[6].indexOf("修") + 1));
        //如果是学生进行该步骤
        if ("s".equals(type)) {
            //学分
            keChengInfo.setCredit(s1[6].substring(s1[6].lastIndexOf(" ") + 1, s1[6].indexOf("</span>")));
            //选课类型，如  自修 重修 再修等
            if (s1.length == 11) {
                keChengInfo.setXklx(s1[8].substring(s1[8].indexOf("\">") + 2, s1[8].indexOf("</")));
            }
            //成绩组成，根据教学班获取
            if (null != cjzcs) {
                keChengInfo.setCjzc((String) cjzcs.get(s1[0]));
            }


        }
        //上课班级类别以及班级号，如果是老师进行该步骤
        if ("t".equals(type)) {
            List<String> bjlbandbjh = new ArrayList<>();
            for (int i = 7; i < s1.length - 2; i++) {
                bjlbandbjh.add(s1[i].substring(s1[i].indexOf("<br>") + 4));
            }
            keChengInfo.setBjlbandbjh(bjlbandbjh);
        }

        return keChengInfo;
    }

    /**
     * 解析课程上课周数
     * “11111111000000000000” to  {1,2,3,4,5,6,7,8}
     *
     * @param zc 课程周数
     * @return 课程周数集合
     */
    public static List<String> parseZCtoWeekNum(String zc) {
        List<String> weekNumList = new ArrayList<>();
        //1.将20位的字符串拆分位20位字节数组
        char[] chars = zc.toCharArray();
        //2.遍历这个数组，值为1就添加对应周数到weekNumList
        for (int i = 0; i < chars.length; i++) {
            if ('1' == chars[i]) {
                weekNumList.add(String.valueOf(i + 1));
            }
        }
        return weekNumList;
    }


    /**
     * 解析教务在线首页html代码，获取校历时间
     *
     * @param html 教务在线首页html代码
     * @return 时间map集合
     */
    public static Map<String, String> parseHtmlToSchoolTime(String html) {
        Map<String, String> sTime = new HashMap<>();

        // 1.转换html字符串为Document对象
        Document doc = Jsoup.parse(html);
        // 2.获取header标签,教务在线的头，包含教务时间。
        Element header = doc.getElementById("header");
        // 3.获取时间内容（字符串）    如： 2019-2020学年1学期 第 16 周 星期 1 2019年12月16日
        String time = header.text();
        // 4.使用正则解析该字符串
        // 4.1 学年
        String xuenian = time.substring(0, time.indexOf("学"));
        sTime.put("semester", xuenian);
        // 4.2 学期
        String xueqi = time.substring(time.indexOf("学年") + 2, time.indexOf("学期"));
        sTime.put("semester", xueqi);
        // 4.3 周
        String week = time.substring(time.indexOf("第") + 2, time.indexOf("周") - 1);
        sTime.put("week", week);
        // 4.4 星期
        String workDay = time.substring(time.indexOf("星期") + 3, time.indexOf("星期") + 4);
        sTime.put("work_day", workDay);
        // 4.5 年
        String year = time.substring(time.indexOf("年", 20) - 4, time.indexOf("年", 20));
        sTime.put("year", year);
        // 4.6 月
        String month = time.substring(time.indexOf("月") - 2, time.indexOf("月"));
        sTime.put("month", month);
        // 4.7 日
        String day = time.substring(time.indexOf("月") + 1, time.indexOf("日"));
        sTime.put("day", day);

        return sTime;

    }

    /**
     * 解析教务在线成绩组成页
     *
     * @param cjzcHtml html页面string字符串
     * @return map<String, String></> key是教学班，value是成绩组成。
     */
    public static Map<String, String> parseHtmlToCJZC(String cjzcHtml) {
        Map<String, String> cjzc = new HashMap<>();
        // 1.jsoup解析
        Document doc = Jsoup.parse(cjzcHtml);
        // 2.获取html页面 所有<tbody>标签
        Elements tbodys = doc.getElementsByTag("tbody");
        // 3.存放数据的标签是第一个，也是唯一一个tbody，所有直接根据索引获取
        Element tbody = tbodys.get(0);
        // 4.获取tbody标签内的所有<tr>标签，<tr>标签代表一门课程的数据。
        Elements trs = tbody.getElementsByTag("tr");
        // 5.遍历所有<tr>标签，取出数据。
        for (Element tr : trs) {
            //5.1 获取<tr>标签里的<td>标签，<td>代表具体数据，第一个<td>是教学班，第五个<td>是课程分类（理论|实验实践），第六个<td>是成绩组成（字符串）
            Elements tds = tr.getElementsByTag("td");

            //教学班
            String jxb = tds.get(0).text();
            //课程分类
            String kcfl = tds.get(4).text();
            //成绩组成
            String cjzcString = tds.get(5).text();

            //5.2将这些数据放到map集合中，课程分类与成绩组成为value，教学班为key
            cjzc.put(jxb, "[" + kcfl + "]" + cjzcString);
        }

        return cjzc;
    }

    /**
     * 解析教务在线学生名单页
     *
     * @param stuListHtml html页面string字符串
     * @return list<ClassStudentInfo></>
     */
    public static List<ClassStudentInfo> parseHtmlToStuList(String stuListHtml) {

        List<ClassStudentInfo> classStuList = new ArrayList<>();

        // 1.jsoup解析
        Document listHtml = Jsoup.parse(stuListHtml);
        // 2.获取页面所以<tbody>标签，装的是名单。实际情况是只有一个tbody标签。
        Elements tbodys = listHtml.getElementsByTag("tbody");
        // 3.根据索引获取第一个也是唯一一个tbody标签
        Element tbody = tbodys.get(0);
        // 4.获取tbody中所有的<tr>标签，一个<tr>标签代表一个学生信息
        Elements trs = tbody.getElementsByTag("tr");
        // 5.遍历tr标签，获取学生数据。
        for (Element tr : trs) {
            // 5.1定义一个学生对象，存放学生数据。
            ClassStudentInfo classStudentInfo = new ClassStudentInfo();
            // 5.2获取<tr>标签里的<td>标签
            Elements tds = tr.getElementsByTag("td");

            //学号，第二个<td>
            classStudentInfo.setXh(tds.get(1).text());
            //姓名，第三个<td>
            classStudentInfo.setXm(tds.get(2).text());
            //性别，第四个<td>
            classStudentInfo.setXb(tds.get(3).text());
            //班级，第五个<td>
            classStudentInfo.setBj(tds.get(4).text());
            //专业，第七个<td>
            classStudentInfo.setZym(tds.get(6).text());
            //学院，第八个<td>
            classStudentInfo.setYxm(tds.get(7).text());
            //年级，第九个<td>
            classStudentInfo.setNj(tds.get(8).text());
            //学籍状态，第十个<td>
            classStudentInfo.setXjzt(tds.get(9).text());
            //选课状态,第十一个<td>
            classStudentInfo.setXkzt(tds.get(10).text());

            //将该学生数据放入list集合
            classStuList.add(classStudentInfo);
        }
        return classStuList;
    }


    /**
     * 获取字符串的编码格
     *
     * @param str 字符串
     * @return 编码类型
     */
    public static String getEncoding(String str) {
        String encode;

        encode = "UTF-16";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }

        encode = "ASCII";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return "字符串<< " + str + " >>中仅由数字和英文字母组成，无法识别其编码格式";
            }
        } catch (Exception ignored) {
        }

        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }

        encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }

        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return encode;
            }
        } catch (Exception ignored) {
        }

        /*
         *......待完善
         */

        return "未识别编码格式";
    }


    /**
     * 解析课程学生名单，获取总人数，不同选课状态的学生人数，不同专业下不同班级的学生人数。
     *
     * @param classStuList 学生名单
     * @return KbStuListData
     */
    public static KbStuListData parseStuListToKbStuListData(List<ClassStudentInfo> classStuList) {

        //1.总人数
        int headcount = classStuList.size();
        //2.选课状态（不同选课状态的人数，key是选课状态，value对应选课状态的人生）
        Map<String, Integer> xkztMap = new HashMap<>();
        //3.专业集合（外层map的key是专业名，value是map。内层map的key是班级号，value是对应班级的号的人数）
        Map<String, Map<String, Integer>> allZyAndBjMap = new HashMap<>();
        //4.正常选课的专业和班级对应人数。这个集合不包含非正常选课学生的专业和班级。
        Map<String, Map<String, Integer>> normalZyAndBj = new HashMap<>();

        //遍历学生名单。分析数据。
        for (ClassStudentInfo classStudentInfo : classStuList) {
            // 2.统计选课状态
            String xkzt = classStudentInfo.getXkzt();//选课状态
            if (xkztMap.containsKey(xkzt)) {
                xkztMap.put(xkzt, xkztMap.get(xkzt) + 1);
            } else {
                xkztMap.put(xkzt, 1);
            }

            //3.统计各专业下各班级人数
            String zym = classStudentInfo.getZym();//专业名
            String bj = classStudentInfo.getBj();//班级号
            //3.1创建专业
            if (!allZyAndBjMap.containsKey(zym)) {
                //2.1.1 集合中没有该专业，将该专业添加到集合。并创建一个属于该专业的班级集合。
                Map<String, Integer> bjMap = new HashMap<>();
                allZyAndBjMap.put(zym, bjMap);
            }
            //3.2取出该专业下的班级集合
            Map<String, Integer> bjMap = allZyAndBjMap.get(zym);
            //3.3往该专业下的班级集合添加数据。
            if (bjMap.containsKey(bj)) {
                bjMap.put(bj, bjMap.get(bj) + 1);
            } else {
                bjMap.put(bj, 1);
            }

            //4.统计正常选课的专业和班级人数。如果是重修，自修，在修就不统计。
            if (!("重修".equals(xkzt) || "自修".equals(xkzt) || "再修".equals(xkzt))) {
                if (!normalZyAndBj.containsKey(zym)) {
                    Map<String, Integer> normalBj = new HashMap<>();
                    normalZyAndBj.put(zym, normalBj);
                }
                Map<String, Integer> norBj = normalZyAndBj.get(zym);
                if (norBj.containsKey(bj)) {
                    norBj.put(bj, norBj.get(bj) + 1);
                } else {
                    norBj.put(bj, 1);
                }
            }

        }

        KbStuListData kbStuListData = new KbStuListData();
        kbStuListData.setHeadcount(headcount);
        kbStuListData.setNumberOfXkzt(xkztMap);
        kbStuListData.setNumberOfZyAndBj(allZyAndBjMap);
        kbStuListData.setNumberOfNormalBj(normalZyAndBj);
        kbStuListData.setStudents(classStuList);

        return kbStuListData;
    }

    /**
     * 解析教务在线的个人服务页，获取用户的学号或者教师号
     * 因为教务在线的个人主页，教师跟学生的大致相同，所以可以用同一套逻辑。只不过教师号在老师端叫“教务帐号” 学生号在学生端叫“学号”
     *
     * @param htmlTea 教务在线个人主页
     * @return 教师号或者学号，如果获取失败返回“false”
     */
    public static String ParseHtmlToteaIdOrXh(String htmlTea) {

        try {
            //1.将html字符串解析为Document对象
            Document parse = Jsoup.parse(htmlTea);
            //2.获取页面中的<tbody>标签。实际情况，只有一个。
            Elements tbodys = parse.getElementsByTag("tbody");
            //3.根据索引0直接获取唯一的一个<tbody>
            Element tbody = tbodys.get(0);
            //4.获取tbody中的所以<tr>标签
            Elements trs = tbody.getElementsByTag("tr");
            //5.循环trs获取数据
            for (Element tr : trs) {
                //获取tr标签下的两个td标签
                Elements tds = tr.getElementsByTag("td");
                //第一个td标签是名称，如（“姓名：、教务帐号：、岗位类别：、等”）
                String tab = tds.get(0).text();
                //第二个标签是数据，如（王斌（信息）、420004、等）
                String value = tds.get(1).text();
                //如果数据名称是“教务帐号”或者“学号”，直接返回具体数据。
                if ("教务帐号：".equals(tab) || "学号：".equals(tab)) {
                    return value;
                }
            }
        } catch (Exception e) {
            logger.error("解析教务在线个人页失败");
            return "false";
        }
        //如果没有，返回“false”
        return "false";
    }

}
