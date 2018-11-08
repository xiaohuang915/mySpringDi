package com.huang.service;

import com.huang.annotation.MyComponent;
import com.huang.applicationContext.AnnotationConfigApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Auther: pc.huang
 * @Date: 2018/7/25 09:30
 * @Description:
 */
@MyComponent
public class UserServiceTest {
    AnnotationConfigApplicationContext applicationContext;
    UserService userService;

    @Before
    public void init() {
        applicationContext = new AnnotationConfigApplicationContext("com.huang");
        userService = applicationContext.getBean("userService", UserService.class);
    }

    @Test
    public void userLogin() {
        userService.userLogin();
    }

    @After
    public void close() {
        applicationContext.close();
    }
}