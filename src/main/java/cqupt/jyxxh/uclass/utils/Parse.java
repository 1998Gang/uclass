package cqupt.jyxxh.uclass.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cqupt.jyxxh.uclass.pojo.ClassInfo;
import cqupt.jyxxh.uclass.pojo.Student;
import cqupt.jyxxh.uclass.pojo.Teacher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.directory.Attributes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于解析相关数据的工具类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 15:53 2019/11/5
 */
public class Parse {

    final static Logger logger= LoggerFactory.getLogger(Parse.class);  //日志


    /**
     * 解析通过LDAP查询返回的学生老师信息（Attributes ）
     *     以下是Attributes（无法直接读取具体数据）的数据格式：{uid=uid: 1655728, edupersonstudentid=eduPersonStudentID: 2017214033, edupersonorgdn=eduPersonOrgDN: 软件工程学院, cn=cn: 彭渝刚}
     *     将其解析为HashMap，以 key-value方式，方后续取用：{ c= 彭渝刚, ui= 1655728,  edupersonorgd= 软件工程学院,  edupersonstudenti= 2017214033}
     *
     * @param attributes
     * @return
     */
    public static HashMap<String,String> ParseAttributes(Attributes attributes){
        //创建一个map集合
        HashMap<String,String> attrsMap=new HashMap<>();
        try{
            //接收传入的attributes（LDAP查询的返回值，转化为字符串用于后续解析，同时去掉字符串前后的 “{” “}” 符号,以及字符串中的空格。
            String ldapBack=attributes.toString().substring(1,attributes.toString().length()-1).replace(" ","");
            //将转化后的字符串 以“，”分割开放入数组
            String ldapBackArry[]=ldapBack.split(",");
            //循环数组 并再次解析键与值
            for (int i=0;i<ldapBackArry.length;i++){
                String key=ldapBackArry[i].substring(0,ldapBackArry[i].indexOf("="));
                String value=ldapBackArry[i].substring(ldapBackArry[i].indexOf(":")+1);
                attrsMap.put(key,value);
            }
        }catch (Exception e){
            logger.error("【解析操作（ParseAttributes）】解析失败，未知错误",e);

        }
        return attrsMap;
    }


    /**
     *转化编码方式   将经过URLecode编码 转换回来
     *     例如：
     *     转化之前：{"code":0,"info":"ok","returnData":[{"xh":"2017214033","xm":"\u5f6d\u6e1d\u521a","xmEn":"Peng Yu Gang ","xb":"\u7537","bj":"13001701","zyh":"1300","zym":"\u8f6f\u4ef6\u5de5\u7a0b","yxh":"13","yxm":"\u8f6f\u4ef6\u5de5\u7a0b\u5b66\u9662","nj":"2017","csrq":"19981030","xjzt":"\u5728\u6821","rxrq":"201709","yxmen":"School of Software","zymEn":"Software Engineering","xz":4,"mz":"\u6c49\u65cf                "}]}
     *     转化之后：{"code":0,"info":"ok","returnData":[{"xh":"2017214033","xm":"彭渝刚","xmEn":"Peng Yu Gang ","xb":"男","bj":"13001701","zyh":"1300","zym":"软件工程","yxh":"13","yxm":"软件工程学院","nj":"2017","csrq":"19981030","xjzt":"在校","rxrq":"201709","yxmen":"School of Software","zymEn":"Software Engineering","xz":4,"mz":"汉族                "}]}
     *
     * @param theString
     * @return
     */
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


    /**
     * 解析学生接口返回的json数据 json对象转为Student对象
     *
     * @param stuJsonInfo    ：学生信息json数据,例如:{"code":0,"info":"ok","returnData":[{"xh":"2017214033","xm":"\u5f6d\u6e1d\u521a","xmEn":"Peng Yu Gang ","xb":"\u7537","bj":"13001701","zyh":"1300","zym":"\u8f6f\u4ef6\u5de5\u7a0b","yxh":"13","yxm":"\u8f6f\u4ef6\u5de5\u7a0b\u5b66\u9662","nj":"2017","csrq":"19981030","xjzt":"\u5728\u6821","rxrq":"201709","yxmen":"School of Software","zymEn":"Software Engineering","xz":4,"mz":"\u6c49\u65cf                "}]}
     * @return
     */
    public static Student ParseJsonToStudent(String stuJsonInfo){
        Student studentInfo=new Student();
        //判断传入参数是否为空
        if ("".equals(stuJsonInfo)||stuJsonInfo==null){
            logger.debug("【解析学生json数据】失败！stuJsonInfo为空");
            return studentInfo ;
        }

        try {
            //创建jackson核心对象Obujectmapper
            ObjectMapper objectMapper=new ObjectMapper();
            //读取json字符串获取顶层的jsonNode对象
            JsonNode jsonNodeTop=objectMapper.readTree(stuJsonInfo);
            //获取returnData的jsonNode
            JsonNode jsonNodeReturnData=jsonNodeTop.get("returnData");
            //取出returnData对象数组中的json对象数据
            //因为学生数据是根据学号查询的，返回的json数据中 returnData数组中也只会有一个对象数据，所以数组索引为0直接读取
            JsonNode stuInfo=jsonNodeReturnData.get(0);
            studentInfo.setXm(stuInfo.get("xm").toString().replace("\"","")); //姓名
            studentInfo.setXb(stuInfo.get("xb").toString().replace("\"","")); //性别
            studentInfo.setXh(stuInfo.get("xh").toString().replace("\"","")); //学号
            studentInfo.setYxm(stuInfo.get("yxm").toString().replace("\"",""));  //学院名（院系名）
            studentInfo.setZym(stuInfo.get("zym").toString().replace("\"",""));  //专业名
            studentInfo.setNj(stuInfo.get("nj").toString().replace("\"",""));    //年级
            studentInfo.setBj(stuInfo.get("bj").toString().replace("\"",""));    //班级
            studentInfo.setXjzt(stuInfo.get("xjzt").toString().replace("\"",""));  //学籍状态
            studentInfo.setMz(stuInfo.get("mz").toString().replace("\"",""));      //民族
            studentInfo.setCsrq(stuInfo.get("csrq").toString().replace("\"",""));  //出生日期

        } catch (IOException e) {
            e.printStackTrace();
        }

        return studentInfo;
    }


    /**
     * 解析访问教师信息接口返回的json字符串为teacher对象
     *
     * @param teaJsonInfo   教师信息的json数据，例如：{"code":0,"info":"ok","returnData":[{"teaId":"030403","teaName":"\u5411\u654f\uff08\u7ecf\uff09","xb":"\u5973","jysm":"\u5e94\u7528\u7ecf\u6d4e\u5b66\u7cfb","yxm":"\u7ecf\u6d4e\u7ba1\u7406\u5b66\u9662","zc":"\u6559\u6388"},
     *                                                                          {"teaId":"080213","teaName":"\u5411\u654f\uff08\u81ea\u52a8\u5316\uff09","xb":"\u7537","jysm":"\u81ea\u52a8\u5316\u4e0e\u673a\u5668\u4eba\u5de5\u7a0b\u7cfb","yxm":"\u81ea\u52a8\u5316\u5b66\u9662","zc":"\u6559\u6388"}]}
     * @return
     */
    public static List<Teacher> ParseJsonToTeacher(String teaJsonInfo){
        //创建教师集合
        List<Teacher> teaList=new ArrayList<>();
        //判断传入参数是否为空
        if(null==teaJsonInfo||"".equals(teaJsonInfo)){
            logger.debug("【解析教师json格式信息（ParseJsonToTeacher）】 失败！参数teaJsonInfo为空");
            return teaList;
        }
        try {

            //创建jackson核心对象Obujectmapper
            ObjectMapper objectMapper=new ObjectMapper();
            //读取json字符串获取顶层的jsonNode对象
            JsonNode jsonNodeTop=objectMapper.readTree(teaJsonInfo);
            //获取returnData的jsonNode
            JsonNode jsonNodeReturnData=jsonNodeTop.get("returnData");
            //循环遍历json对象数组的对象
            /*因为教师信息是根据姓名查询的，存在重名的教师，会查出多条数据，返回的json数据 reternData数组里面有多名老师信息，所以需要循环读取到教师集合里面
             * {"code":0,"info":"ok","returnData":[{"teaId":"030403","teaName":"向敏（经）","xb":"女","jysm":"应用经济学系","yxm":"经济管理学院","zc":"教授"},
             *                                     {"teaId":"080213","teaName":"向敏（自动化）","xb":"男","jysm":"自动化与机器人工程系","yxm":"自动化学院","zc":"教授"}]}
              */
            for (int i=0;i<jsonNodeReturnData.size();i++){
                Teacher teacher=new Teacher();
                JsonNode teaInfo=jsonNodeReturnData.get(i);
                teacher.setTeaName(teaInfo.get("teaName").toString().replace("\"",""));//姓名
                teacher.setTeaId(teaInfo.get("teaId").toString().replace("\"",""));//教师号
                teacher.setXb(teaInfo.get("xb").toString().replace("\"",""));//性别
                teacher.setJysm(teaInfo.get("jysm").toString().replace("\"",""));//教研室
                teacher.setYxm(teaInfo.get("yxm").toString().replace("\"",""));//学院
                teacher.setZc(teaInfo.get("zc").toString().replace("\"",""));//职称
                teaList.add(teacher);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return teaList;
    }


    /**
     * 判断b字符串是否包含a字符串中全部字符
     *
     * @param a  被包含字符串
     * @param b  包含字符串
     * @return
     */
    public static boolean isbaohan(String a,String b){
        boolean flage=true;
        //将字符串a转换为char数组
        char[] chars = a.toCharArray();
        //循环判断chars中字符是否包含在字符串内
        for (int i=0;i<chars.length;i++){
            if (b.indexOf(chars[i])==-1){
                //如果有chars中字符在字符串b中没有，改flage为false
                flage=false;
            }
        }
        return flage;
    }

    public static List<ClassInfo>  parseHtmlToClassInfo(String html){
        List<ClassInfo> classInfoList=null;

        //1.解析html
        Document doc = Jsoup.parse(html);
        //2.根据id获取“kbStuTabs-table”课表形式的课表
        Element kbStuTabsTable = doc.getElementById("kbStuTabs-table");
        //2.根据class获取 printTable
        Elements printTable = kbStuTabsTable.getElementsByClass("printTable");
        //3.获取课表body


        return classInfoList;

    }


}
