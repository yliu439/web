package com.liuyang.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.liuyang.web"},
               useDefaultFilters = false,
               includeFilters={
                       @ComponentScan.Filter(type=FilterType.ANNOTATION, classes={Controller.class})
               })
public class MvcConfig implements WebMvcConfigurer {

    private final Logger logger = LoggerFactory.getLogger(WebMvcConfigurer.class);

    //Static Resources  <mvc:resources mapping="/resources/**" location="/public, classpath:/static/" cache-period="31556926" />
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.debug("配置静态资源映射........");
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/public", "classpath:/static/")
                .setCachePeriod(31556926);
    }

    //Default Servlet <mvc:default-servlet-handler/>
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        logger.debug("配置默认servlet........");
        configurer.enable();
    }

    //<mvc:interceptors/>
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.debug("配置默认拦截器........");
        registry.addInterceptor(new CommonInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
        //registry.addInterceptor(new LocaleChangeInterceptor());
        //registry.addInterceptor(new ThemeChangeInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
        //registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/secure/*");
    }
}
