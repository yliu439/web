package com.liuyang.web.core;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;

public class CustomWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private final Logger logger = LoggerFactory.getLogger(CustomWebApplicationInitializer.class);

    @Override
    protected Class<?>[] getRootConfigClasses() {
//        return new Class[0];
        logger.debug("开始初始化DispatcherServlet>>>getRootConfigClasses");
        return new Class<?>[]{MainConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
//        return new Class[0];
        logger.debug("开始初始化DispatcherServlet>>>getServletConfigClasses");
        return new Class<?>[]{MvcConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
//        return new String[0];
        logger.debug("开始初始化DispatcherServlet>>>getServletMappings");
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new HiddenHttpMethodFilter(), new CharacterEncodingFilter()};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        logger.debug("开始配置监控Filter......");
        FilterRegistration.Dynamic druidWebStatFilter = servletContext.addFilter("DruidWebStatFilter", WebStatFilter.class);
        druidWebStatFilter.setInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        druidWebStatFilter.addMappingForUrlPatterns(null, false, "/*");
        logger.debug("开始配置监控Servlet......");
        ServletRegistration.Dynamic druidStatView=servletContext.addServlet("DruidStatView", StatViewServlet.class);
        druidStatView.setInitParameter("resetEnable","true");
        druidStatView.setInitParameter("loginUsername","ly");
        druidStatView.setInitParameter("loginPassword","1");
        druidStatView.addMapping("/druid/*");
    }
}
