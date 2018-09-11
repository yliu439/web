package com.liuyang.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


public class CustomWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private final Logger logger = LoggerFactory.getLogger(CustomWebApplicationInitializer.class);

    @Override
    protected Class<?>[] getRootConfigClasses() {
//        return new Class[0];
        logger.debug("开始初始化DispatcherServlet>>>getRootConfigClasses");
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
//        return new Class[0];
        logger.debug("开始初始化DispatcherServlet>>>getServletConfigClasses");
        return new Class<?>[] { MvcConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
//        return new String[0];
        logger.debug("开始初始化DispatcherServlet>>>getServletMappings");
        return new String[] { "/" };
    }
}
