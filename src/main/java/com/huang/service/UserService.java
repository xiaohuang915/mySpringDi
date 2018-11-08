package com.huang.service;

import com.huang.annotation.MyAutowired;
import com.huang.annotation.MyComponent;
import com.huang.entity.User;

/**
 * @Auther: pc.huang
 * @Date: 2018/7/24 15:59
 * @Description:
 */
@MyComponent
public class UserService {
    @MyAutowired
    User user;
    @MyAutowired
    User user1;

    public void userLogin() {
        System.out.println("用户1："+user);
        user.login();
        System.out.println("用户2："+user1);
        user1.login();
    }
}
