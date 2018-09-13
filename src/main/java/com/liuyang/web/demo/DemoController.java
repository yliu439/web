package com.liuyang.web.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DemoController implements ApplicationContextAware {

    final Logger logger = LoggerFactory.getLogger(DemoController.class);
    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping(value="/index")
    public String index(){
        return "Hello LiuYang!";
    }

    @GetMapping(value="/admin")
    public String admin(){
        return "Hello Admin!";
    }

    @GetMapping(value="/add")
    public int add(@RequestParam String name,@RequestParam String alise, @RequestParam String birthday){
        Demo demo=new Demo();
        demo.setName(name);
        demo.setAlias(alise);
        if(StringUtils.hasText(birthday))
            demo.setBirthday(string2LocalDateTime(birthday,"yyyy-MM-dd HH:mm:ss"));
        return demoService.add(demo);
    }

    //要求返回XML文件
    //需要配置对应的MappingJackson2XmlHttpMessageConverter进行处理
    @GetMapping(value="/beans",produces = MediaType.APPLICATION_XML_VALUE)
    public List<String> getBeans(){
        Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).forEach(logger::debug);
        return Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());
    }

    @GetMapping(value="/all")
    public List<Demo> getAll(){
        //Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).forEach(logger::debug);
        //return Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());
        return demoService.getAll();
    }

    private LocalDateTime string2LocalDateTime(String time, String format){
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(format));
    }

    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DemoController.applicationContext=applicationContext;
    }
}
