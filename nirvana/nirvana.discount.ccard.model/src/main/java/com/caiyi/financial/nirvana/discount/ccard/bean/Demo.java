package com.caiyi.financial.nirvana.discount.ccard.bean;

/**
 * Created by been on 16/4/21.
 */
public class Demo {
    private String name;
    private int age;
    private String dbwritetime;
    private String clientwritetime;

    public Demo() {
    }

    public String getDbwritetime() {
        return dbwritetime;
    }

    public void setDbwritetime(String dbwritetime) {
        this.dbwritetime = dbwritetime;
    }

    public String getClientwritetime() {
        return clientwritetime;
    }

    public void setClientwritetime(String clientwritetime) {
        this.clientwritetime = clientwritetime;
    }

    public Demo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", dbwritetime='" + dbwritetime + '\'' +
                ", clientwritetime='" + clientwritetime + '\'' +
                '}';
    }
}
