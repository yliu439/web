package com.liuyang.web.demo;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class DemoController implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(DemoController.class);
    private final DemoService demoService;
    private final Environment env;

    public DemoController(DemoService demoService, Environment env) {
        this.demoService = demoService;
        this.env = env;
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

    @GetMapping(value="/all")
    public List<Demo> getAll(){
        //Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).forEach(logger::debug);
        //return Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());
        return demoService.getAll();
    }

    private static LocalDateTime string2LocalDateTime(final String time, final String format){
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(format));
    }
//*******************************获取ApplicationContext方法*************************************************************
    //获取ApplicationContext方法一(实现ApplicationContextAware)
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DemoController.applicationContext=applicationContext;
    }
    //要求返回XML文件
    //需要配置对应的MappingJackson2XmlHttpMessageConverter进行处理
    @GetMapping(value="/beans",produces = MediaType.APPLICATION_XML_VALUE)
    public List<String> getBeans(){
        Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).forEach(logger::debug);
        return Arrays.stream(DemoController.applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());
    }

    //获取ApplicationContext方法二
    @Autowired
    private ServletContext servletContext;
    @GetMapping(value="/b")
    public List<String> getb(){
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return Arrays.stream(applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());
    }

    //获取ApplicationContext方法三
    @GetMapping(value="/bb")
    public List<String> getbb(HttpServletRequest request){
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return Arrays.stream(applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());
    }

    //获取ApplicationContext方法四
    @GetMapping(value="/bbb")
    public List<String> getbbb(HttpServletRequest request){
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        if (applicationContext != null) {
            return Arrays.stream(applicationContext.getBeanDefinitionNames()).collect(Collectors.toList());
        }
        return null;
    }

//*************************************************文件上传*************************************************************
    @RequestMapping(value="/up")
    public String upload(HttpServletRequest request, MultipartFile uploadFile) throws Exception {
        if(!uploadFile.isEmpty()) {
            //String path = WebUtils.getRealPath(request.getServletContext(), "jj\\hh");//Web工程的根路径
            String path=WebUtils.getRealPath(request.getServletContext(), env.getProperty("upload.path.teacher","upload/teacher/"));
            logger.info("path={}",path);
            return demoService.upload(path, uploadFile);
        }
        return "fail";
    }

    @RequestMapping(value="/ups")
    public String uploads(HttpServletRequest request, MultipartFile[] uploadFiles) throws Exception {
        if(uploadFiles!=null&&uploadFiles.length>0){
            int fileNum=uploadFiles.length;
            if(fileNum>5) return "一次上传文件数量不等超过5个！";
            for(MultipartFile uploadFile:uploadFiles){
                if(!uploadFile.isEmpty()) {
                    String path=WebUtils.getRealPath(request.getServletContext(), env.getProperty("upload.path.teacher","upload/teacher/"));
                    logger.info("path={}",path);
                    try{
                        demoService.upload(path, uploadFile);
                    }catch (Exception e){
                        logger.error(e.getLocalizedMessage(),e);
                    }
                }
            }
        }
        return "fail";
    }

//*************************************************异步访问*************************************************************

    /*
     * 通过@Async 注解配置异步
     * 满足条件：
     *  根容器需要添加开启异步的注解：@EnableAsync
     *  根容器中需要配置TaskExecutor Bean(建议配置ThreadPoolTaskExecutor)，如果根容器中不配置任何TaskExecutor，则系统报异常，并自动创建SimpleAsyncTaskExecutor在进行异步多线程处理
     *  异步方法配置在Service中(异步类的bean不能被重复扫描，而且只能被sping容器的上下文扫描，不能被spingmvc扫描，否则异步失效)
     */

    //@Async异步方法配置在Controller中,该配置异步不起作用，异步线程与主线程完全相同
    @RequestMapping(value = "/AsyncWithoutResponse1", method = RequestMethod.GET)
    public void AsyncWithoutResponse1() throws Exception{
        logger.debug("请求{}方法,线程id:{},线程名称:{}", "AsyncWithoutResponse1", Thread.currentThread().getId(),Thread.currentThread().getName());
        processAsync();
    }
    @Async
    void processAsync() throws Exception {
        logger.info("异步线程,线程id:{},线程名称:{}", Thread.currentThread().getId(), Thread.currentThread().getName());
        Thread.sleep(3000);
    }

    //@Async异步方法配置在Service中,该配置异步线程起作用，调用的是在根容器中配置的TaskExecutor执行的线程
    @RequestMapping(value = "/AsyncWithoutResponse2", method = RequestMethod.GET)
    public void AsyncWithoutResponse2() throws Exception{
        logger.debug("请求{}方法,线程id:{},线程名称:{}", "AsyncWithoutResponse2", Thread.currentThread().getId(),Thread.currentThread().getName());
        demoService.processAsync(0);
    }
    //循环调用异步线程
    @RequestMapping(value = "/AsyncWithoutResponse3", method = RequestMethod.GET)
    public void AsyncWithoutResponse3() throws Exception{
        logger.debug("请求{}方法,线程id:{},线程名称:{}", "AsyncWithoutResponse3", Thread.currentThread().getId(),Thread.currentThread().getName());
        for(int i=0;i<3;i++){
            demoService.processAsync(i);
        }
    }
    //循环调用异步线程，带有返回值
    @RequestMapping(value = "/AsyncWithResponse", method = RequestMethod.GET)
    public String AsyncWithResponse() throws Exception{
        logger.debug("请求{}方法,线程id:{},线程名称:{}", "AsyncWithResponse", Thread.currentThread().getId(),Thread.currentThread().getName());
        for(int i=0;i<3;i++){
            demoService.processAsync(i);
        }
        return "成功！";
    }


    @RequestMapping(value = "/callable", method = RequestMethod.GET)
    public Callable<String> getCallableMessage() {
        logger.debug("请求{}方法,线程id:{},线程名称:{}", "callable", Thread.currentThread().getId(),Thread.currentThread().getName());
        return () -> {
            logger.info("异步线程,线程id:{},线程名称:{}",Thread.currentThread().getId(),Thread.currentThread().getName());
            Thread.sleep(10000);
            return "Wo shi LY";
        };
    }

    @RequestMapping(value = "/deferred", method = RequestMethod.GET)
    public DeferredResult<String> getDeferredResult() throws InterruptedException {
        logger.debug("请求{}方法,线程id:{},线程名称:{}", "deferred", Thread.currentThread().getId(),Thread.currentThread().getName());
        final DeferredResult<String> def = new DeferredResult<String>(6000L, "TIME_OUT_RESULT");

        def.onTimeout(() -> {
            logger.error("请求处理超时");
            def.setErrorResult("TIME_OUT_RESULT");
        });

        def.onCompletion(() -> logger.debug("请求结束"));

        new Thread(() -> {
            def.setResult(processAsyncResult());
            def.setResultHandler((result) -> logger.debug("DeferredResultHandler.handleResult[{}]", def.getResult()));
        }).start();

        logger.debug("释放线程id:{}", Thread.currentThread().getId());
        return def;
    }

    @RequestMapping(value = "/asyncTask", method = RequestMethod.GET)
    public WebAsyncTask<String> getAsyncTask() {
        WebAsyncTask<String> asyncTask = new WebAsyncTask<>(6000L, () -> processAsyncResult());
        asyncTask.onCompletion(() -> logger.debug("异步任务结束"));
        asyncTask.onTimeout(() -> {
            logger.debug("异步任务结束");
            return "TIME_OUT_RESULT";
        });
        return asyncTask;
    }
    private String processAsyncResult() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "ASYNC_RESULT";
    }

//*********************************************HTTP Streaming(异步响应)*************************************************

    @GetMapping("/events")
    public ResponseBodyEmitter handle() throws Exception{
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        processAsyncResult(emitter,5L);
        return emitter;
    }

    @Async
    void processAsyncResult(ResponseBodyEmitter emitter, Long  eventNumber) throws Exception{
        for (long i = 1; i <= eventNumber; i++) {
            logger.info("**********************************{}*********************************",i);
            Thread.sleep(3000);
            emitter.send("msg" + i + "\r\n");
        }
        emitter.complete();
    }


//    @GetMapping("/events")
//    public ResponseBodyEmitter handle() throws Exception{
//        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//            new Thread(() -> {
//                try{
//                    for(int i=0;i<5;i++){
//                        logger.debug("jjjjjjjjjjjjjjjjjjjjjj");
//                        Thread.sleep(3000);
//                        emitter.send("Emitter");
//                        if(i==4) emitter.complete();
//                    }
//                }catch (Exception e){
//                    logger.error(e.getLocalizedMessage());
//                }
//            }).start();
//            //processAsyncResult(emitter,i);
//        return emitter;
//    }
//    void processAsyncResult(ResponseBodyEmitter emitter, int i) throws Exception{
//        try {
//            Thread.sleep(5000);
//            emitter.send("Emitter"+i);
//            if(i==4) emitter.complete();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }

}
