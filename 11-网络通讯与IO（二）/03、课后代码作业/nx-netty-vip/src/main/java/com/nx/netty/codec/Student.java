package com.nx.netty.codec;

import java.io.Serializable;

/**
 * @author mkp
 * @Description:
 */
public class Student implements Serializable {

    private String name ;

    private Integer age ;


    public Student(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}
