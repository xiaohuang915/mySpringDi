package com.huang.applicationContext;

import com.huang.annotation.MyAutowired;
import com.huang.annotation.MyComponent;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: pc.huang
 * @Date: 2018/7/24 16:03
 * @Description:
 */
public class AnnotationConfigApplicationContext {

    //存储类对象
    private Map<String, Class<?>> beanDefinationFactory = new ConcurrentHashMap<>();
    //存储单例对象
    private Map<String, Object> singletonbeanFactory = new ConcurrentHashMap<>();

    //
    public AnnotationConfigApplicationContext(String packageName) {
        scanPackage(packageName);
        dependencyInjection();
    }

    /**
     * @Description: 依赖注入
     * @param: []
     * @return: void
     * @auther: pc.huang
     * @date: 2018/7/24 16:06
     */
    private void dependencyInjection() {
        //获取容器中所有类对象
        Collection<Class<?>> classes = beanDefinationFactory.values();
        for (Class<?> clz : classes) {
            String clzName = clz.getName();
            String beanId = toLowerFirstWord(clzName.substring(clzName.lastIndexOf(".") + 1));
            //获取类中所有属性
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) {
                //如果有autowired，进行注入操作
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    try {
                        //获取属性名称
                        String fieldName = field.getName();
                        System.out.println("属性名：" + fieldName);
                        Object fieldBean = null;
                        //根据属性从容器中取出对象，不为空，则赋值
                        if (beanDefinationFactory.get(fieldName) != null) {
                            fieldBean = getBean(fieldName, field.getType());
                        } else {
                            //否则按照类型从容器中取出对象注入
                            String type = field.getType().getName();
                            type = type.substring(type.lastIndexOf(".") + 1);
                            String fieldBeanId = toLowerFirstWord(type);
                            System.out.println("属性类型id：" + fieldBeanId);
                            fieldBean = getBean(fieldBeanId, field.getType());
                        }
                        System.out.println("需要注入的属性值：" + fieldBean);
                        if (null != fieldBean) {
                            Object bean = getBean(beanId, clz);
                            field.setAccessible(true);
                            field.set(bean, fieldBean);
                            System.out.println("注入成功!");
                        } else {
                            System.out.println("注入失败!");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @Description: 获取bean
     * @param: [fieldName, type]
     * @return: java.lang.Object
     * @auther: pc.huang
     * @date: 2018/7/24 17:58
     */
    public Object getBean(String beanId) {
        //根据传入beanId获取类对象
        Class<?> cls = beanDefinationFactory.get(beanId);
        //根据类对象获取其定义的注解
        MyComponent annotation = cls.getAnnotation(MyComponent.class);
        //获取注解的scope属性值
        String scope = annotation.scope();
        try {
            //如果scope等于singleton,创建单例对象
            if ("singleton".equals(scope) || "".equals(scope)) {
                //判断容器中是否已有该对象的实例,如果没有,创建一个实例对象放到容器中
                if (singletonbeanFactory.get(beanId) == null) {
                    Object instance = cls.newInstance();
                    setFieldValues(cls, instance);
                    singletonbeanFactory.put(beanId, instance);
                }
                //根据beanId获取对象并返回
                return singletonbeanFactory.get(beanId);
            }
            //如果scope等于prototype,则创建并返回多例对象
            if ("prototype".equals(scope)) {
                Object instance = cls.newInstance();
                setFieldValues(cls, instance);
                return instance;
            }
            //目前仅支持单例和多例两种创建对象的方式
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //如果遭遇异常，返回null
        return null;
    }

    private void setFieldValues(Class<?> cls, Object instance) {

    }

    /**
     * 此为重载方法，根据传入的class对象在内部进行强转，
     * 返回传入的class对象的类型
     */
    public <T> T getBean(String beanId, Class<T> c) {
        return (T) getBean(beanId);
    }

    /**
     * @Description: 扫描包
     * @param: [packageName]
     * @return: void
     * @auther: pc.huang
     * @date: 2018/7/24 16:06
     */
    private void scanPackage(String packageName) {
        System.out.println("扫描的包名为：" + packageName);
        String pkgDir = packageName.replaceAll("\\.", "/");
        URL url = this.getClass().getClassLoader().getResource(pkgDir);
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanPackage(packageName + "." + file.getName());
            } else {
                String fileName = file.getName().replace(".class", "");
                String className = packageName + "." + fileName;
                try {
                    Class<?> clz = Class.forName(className);
                    if (clz.isAnnotationPresent(MyComponent.class)) {
                        beanDefinationFactory.put(toLowerFirstWord(fileName), clz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        }
    }

    /**
     * @Description: 字符串首字母小写
     * @param: [name]
     * @return: java.lang.String
     * @auther: pc.huang
     * @date: 2018/7/19 15:59
     */
    private String toLowerFirstWord(String name) {
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

    /**
     * @Description: 释放工厂资源
     * @param: []
     * @return: void
     * @auther: pc.huang
     * @date: 2018/7/25 9:27
     */
    public void close() {
        beanDefinationFactory.clear();
        beanDefinationFactory = null;
        singletonbeanFactory.clear();
        singletonbeanFactory = null;
    }
}
