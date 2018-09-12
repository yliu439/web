package com.liuyang.web.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    final Logger logger = LoggerFactory.getLogger(DemoService.class);
    private final DemoDao demoDao;

    public DemoService(DemoDao demoDao) {
        this.demoDao = demoDao;
    }

    public int add(Demo demo){
        return demoDao.insert(demo);
    }
}
