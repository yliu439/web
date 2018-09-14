package com.liuyang.web.core;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.liuyang.web"},
               useDefaultFilters = false,
               includeFilters={
                       @ComponentScan.Filter(type=FilterType.ANNOTATION, classes={Controller.class})
               })
public class MvcConfig implements WebMvcConfigurer {

    private final Logger logger = LoggerFactory.getLogger(MvcConfig.class);

    private final Environment env;
    @Autowired
    public MvcConfig(Environment env) {
        this.env = env;
    }

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
        logger.debug("配置拦截器........");
        registry.addInterceptor(new CommonInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
        //registry.addInterceptor(new LocaleChangeInterceptor());
        //registry.addInterceptor(new ThemeChangeInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
        //registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/secure/*");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        logger.debug("配置消息转换器........");
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                //.dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .modulesToInstall(new ParameterNamesModule())
                .modulesToInstall(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);//该设置将LocalDatatime转换成1979-10-07T13:45:23格式，如果不做该操作返回的是数组[1994,10,7]，该操作结合对象的@JsonFormat注解可以完美进行需要的格式化
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        //converters.add(new MappingJackson2HttpMessageConverter());
//        converters.add(new MappingJackson2XmlHttpMessageConverter());
//        converters.add(new MappingJackson2SmileHttpMessageConverter());
//        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
    }

    //该组件需要org.apache.commons.fileupload.，参见CommonsMultipartResolver源码
    @Bean(name="multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver(){
        logger.debug("配置文件上传解析器........");
        CommonsMultipartResolver commonsMultipartResolver =new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding(env.getProperty("multipart.resolver.default-encoding","UTF-8"));
        commonsMultipartResolver.setMaxUploadSize(env.getProperty("multipart.resolver.max-upload-size",Long.class,10000000L));
        commonsMultipartResolver.setPreserveFilename(env.getProperty("multipart.resolver.preserve-filename",Boolean.class,false));
        return commonsMultipartResolver;
    }

    @Bean(name="viewResolver")
    public InternalResourceViewResolver internalResourceViewResolver() throws Exception{
        logger.debug("配置视图解析器........");
        InternalResourceViewResolver internalResourceViewResolver =new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix(env.getProperty("view.resolver.prefix",""));
        internalResourceViewResolver.setSuffix(env.getProperty("view.resolver.suffix"));
        internalResourceViewResolver.setViewClass(Class.forName(env.getProperty("view.resolver.view-class")));
        return internalResourceViewResolver;
    }

//    @Bean
//    public StringHttpMessageConverter stringHttpMessageConverter(){
//        StringHttpMessageConverter stringHttpMessageConverter=new StringHttpMessageConverter();
//        MediaType mediaType = new MediaType("text", "plain", Charset.forName("UTF-8"));
//        List<MediaType> types = new ArrayList<MediaType>();
//        types.add(mediaType);
//        stringHttpMessageConverter.setSupportedMediaTypes(types);
//        return stringHttpMessageConverter;
//    }


    /**
     * Spring内部自定义线程池,可以对@Async注解以及Controler返回的Callable,WebAsyncTask和DeferredResult等Spring内异步线程的支持
     * 当一个任务通过execute(Runnable)方法欲添加到线程池时：
     * 如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
     * 如果此时线程池中的数量等于 corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
     * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
     * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 handler所指定的策略来处理此任务。
     * 也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程 maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
     * 当线程池中的线程数量大于 corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。
     */
    @Bean(name="mvcPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        logger.debug("配置MVC异步线程池........");
        ThreadPoolTaskExecutor mvcExecutor = new ThreadPoolTaskExecutor();
        mvcExecutor.setCorePoolSize(10); // 线程池维护线程的最少数量
        mvcExecutor.setMaxPoolSize(20); // 线程池维护线程的最大数量
        mvcExecutor.setKeepAliveSeconds(300); // 空闲线程的最长保留时间,超过此时间空闲线程会被回收
        mvcExecutor.setQueueCapacity(100); // 线程池所使用的缓冲队列
        mvcExecutor.setThreadNamePrefix("MVC-ThreadPool#");
        // rejection-policy：当线程池线程已达到最大值且任务队列也满了的情况下，如何处理新任务
        // CALLER_RUNS：这个策略重试添加当前的任务，他会自动重复调用 execute() 方法，直到成功
        mvcExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        mvcExecutor.afterPropertiesSet();//Calls { after the container applied all property values.
        return mvcExecutor;
    }
    /**
     * 配置异步访问
     * 三种异步访问方式：
     *  Callable和WebAsyncTask 共用一种拦截器CallableProcessingInterceptor，因为WebAsyncTask就是对Callable的一个封装
     *  DeferredResult DeferredResult使用DeferredResultProcessingInterceptor作为拦截器
     *
     *  对于Callable来说，很简单，直接返回一个Callable对象，将要处理的逻辑放在Callable的run方法内，
     *  当SpringMVC检测到返回的是Callable类型的对象时，会使用我们定义的线程池内的线程去执行此Callable。
     *
     *  对于DeferredResult来说，关键点是要在一个线程内调用DeferredResult的setResult（T）方法，
     *  也就是说我们需要在Controller方法内就启动线程，而不能返回一个任务由SpringMVC来启动。
     *
     *  WebAsyncTask和Callable类似，是对Callable的一个封装，可以增加处理超时的逻辑以及处理完成后的逻辑等
     *
     *  SpringMVC监测Controller方法返回的对象类型，如果是Callable,使用CallableMethodReturnValueHandler去处理；
     *  如果是DeferredResult，使用DeferredResultMethodReturnValueHandler处理；
     *  如果是WebAsyncTask，则使用AsyncTaskMethodReturnValueHandler处理。
     *  他们最终都使用WebAsyncManager这个类来处理异步请求，这个类封装了HttpServletRequest
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        logger.debug("配置异步拦截器........");
        configurer.setDefaultTimeout(50 * 1000); //设置默认的超时时间
        configurer.setTaskExecutor(threadPoolTaskExecutor());  //设置异步请求使用的线程池
        //注册异步请求的拦截器
        configurer.registerCallableInterceptors(new CustomCallableProcessingInterceptor());
        configurer.registerDeferredResultInterceptors(new CustomDeferredResultProcessingInterceptor());
    }

}
