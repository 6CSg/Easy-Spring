package com.csg.springframework.test.bean;

import com.csg.springframework.beans.factory.annotation.Autowired;
import com.csg.springframework.stereotype.Component;

@Component
public class Wife {
    @Autowired
    private Husband husband;

    public String callHusband() {
        return "Wife.husband: " + husband;
    }
}
