package cqupt.jyxxh.uclass.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 删除用户操作类
 *
 *
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 8:04 2019/11/12
 */

@Service
public class DeleteUser {

    @Autowired
    private OperationBind operationBind;

    @Autowired
    private OperationUserInfo operationUserInfo;

    /**
     * 删除用户信息，和绑定信息
     *
     * @param openid
     * @return
     */
    @Transactional   //注解开启事务
    public boolean deleteUserinfoAndBind(String openid){
        //定义操作返回结果
        boolean flage=false;
        //删除用户信息
        boolean b = operationUserInfo.deleteUserInfo(openid);
        boolean b1 = operationBind.deleteBind(openid);
        //判断两个删除操作成功
        if (b==true&&b1==true){
            //两个删除操作都成功，删除绑定用户操作成功。
            flage=true;
        }
        return flage;
    }

}
