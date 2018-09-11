package com.liuyang.web.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @GetMapping(value="/index")
    public String index(){
        return "Hello LiuYang!";
    }

    @GetMapping(value="/admin")
    public String admin(){
        return "Hello Admin!";
    }
}
