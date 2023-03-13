package com.csg.springframework.test;

import com.csg.springframework.context.support.ClassPathXmlApplicationContext;
import com.csg.springframework.test.bean.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ApiTest {
    @Test
    public void test_annotation() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println(userService.queryUserInfo());
    }
    @Test
    public void test_aop() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-aop.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println(userService.queryUserInfo());
    }

    @Test
    public void test_circle() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring2.xml");
        Wife wife = applicationContext.getBean("wife", Wife.class);
        Husband husband = applicationContext.getBean("husband", Husband.class);
        System.out.println(wife.callHusband());
        System.out.println(husband.callWife());

    }

    @Test
    public void test_converter() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring_converter.xml");
        Student student = applicationContext.getBean("student", Student.class);
        System.out.println(student);
    }
}
