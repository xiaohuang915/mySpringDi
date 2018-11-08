package com.huang.entity;

import com.huang.annotation.MyComponent;
import com.huang.annotation.MyValue;

/**
 * @Auther: pc.huang
 * @Date: 2018/7/24 15:55
 * @Description:
 */
@MyComponent
public class User {
    @MyValue("1")
    private Integer id;
    @MyValue("张三")
    private String name;
    @MyValue("123")
    private String password;
    public User(){
        System.out.println("无参方法执行");
    }
    public void login(){
        System.out.println("用户登录：id=" + id + ", name=" + name + ", password=" + password);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
