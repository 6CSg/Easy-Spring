package com.csg.springframework.test.bean;

import com.csg.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("userDao")
public class UserDao {
    private static Map<String, String> hashMap = new HashMap<>();

    static {
        hashMap.put("10001", "victorG，天津，天大");
        hashMap.put("10002", "八杯水，上海，尖沙咀");
        hashMap.put("10003", "阿毛，香港，铜锣湾");
    }
    public String queryUserName(String uId) {
        return hashMap.get(uId);
    }

}