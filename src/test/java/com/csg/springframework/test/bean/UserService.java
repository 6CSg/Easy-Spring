package com.csg.springframework.test.bean;

import com.csg.springframework.beans.factory.annotation.Autowired;
import com.csg.springframework.beans.factory.annotation.Value;
import com.csg.springframework.stereotype.Component;

import java.util.Random;

@Component("userService")
public class UserService implements IUserService {
    @Value("${token}")
    private String token;
    @Autowired
    private UserDao userDao;

    public String queryUserInfo() {
        System.out.println("目标方法执行了：");
        return userDao.queryUserName("10001") + "__" + token;
    }

    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "注册用户：" + userName + " success！";
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
