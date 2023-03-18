package com.csg.springframework.test.bean;

import com.csg.springframework.beans.factory.annotation.Resource;
import com.csg.springframework.stereotype.Component;

@Component
public class Wife {
    @Resource(name = "husband")
    private Husband husband;

    public String callHusband() {
        return "Wife.husband: " + husband;
    }
}
