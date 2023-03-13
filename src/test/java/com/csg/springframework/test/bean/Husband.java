package com.csg.springframework.test.bean;

import com.csg.springframework.beans.factory.annotation.Autowired;
import com.csg.springframework.stereotype.Component;

@Component
public class Husband {
    @Autowired
    private Wife wife;

    public String callWife() {
        return "Husband.wife: " + wife;
    }
}
