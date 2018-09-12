package com.liuyang.web.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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

    @GetMapping(value="/beans")
    public List<Object> getBeans(){
        Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).forEach(logger::debug);
        return null;
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
