package com.liuyang.web.ztest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Test1 {

    private ObjectMapper objectMapper = new ObjectMapper();

    private TestDemo createTestDemo(){
        Wife wife=new Wife();
        wife.setId(1L);
        wife.setName("黄凌");
        wife.setBirthday(LocalDateTime.now());
        List<Wife> list=new ArrayList<>();
        list.add(wife);
        TestDemo testDemo=new TestDemo();
        testDemo.setId(100L);
        testDemo.setName("刘洋");
        testDemo.setBirthday(LocalDateTime.now());
        testDemo.setOnlySetter("OnlySetter");
        testDemo.setEmptyString("");
        testDemo.setWifeList(list);
        return testDemo;
    }

    private void one() throws Exception{
        TestDemo testDemo=createTestDemo();
        System.out.println(objectMapper.writeValueAsString(testDemo));
        //log.info(objectMapper.writeValueAsString(testDemo));
    }

    private void two() throws Exception{
        //处理LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        //objectMapper.configure(SerializationFeature.INDENT_OUTPUT,true);
        TestDemo testDemo=createTestDemo();
        System.out.println(objectMapper.writeValueAsString(testDemo));
    }

    private void three() throws Exception{
        objectMapper.registerModule(new JavaTimeModule());
        TestDemo testDemo=createTestDemo();
        System.out.println(objectMapper.writerFor(new TypeReference<TestDemo>(){}).writeValueAsString(testDemo));
    }

    private void four() throws Exception{
        objectMapper.registerModule(new JavaTimeModule());
        TestDemo testDemo=createTestDemo();
        String json=objectMapper.writeValueAsString(testDemo);
        System.out.println(json);
        TestDemo result1=objectMapper.readValue(json,TestDemo.class);
        System.out.println(result1.getWifeList().get(0).getName());
        TestDemo result2=objectMapper.readValue(json,new TypeReference<TestDemo>(){});
        System.out.println(result2.getWifeList().get(0));
    }

//    new TypeReference<List<Animal>

    public static void main(String[] args) throws Exception{
        Test1 test1=new Test1();
        //test1.two();
        //test1.three();
        test1.four();
    }
}
