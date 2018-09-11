package com.liuyang.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Configuration
@ComponentScan(basePackages = {"com.liuyang.web"},
               useDefaultFilters = false,
               includeFilters={
                       @ComponentScan.Filter(type= FilterType.ANNOTATION, classes={Service.class,Repository.class})
               })
public class MainConfig {
    private final Logger logger = LoggerFactory.getLogger(MainConfig.class);


}
