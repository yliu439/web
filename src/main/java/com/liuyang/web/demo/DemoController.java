package com.liuyang.web.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.stream.Collectors;

@RestController
public class DemoController implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(DemoController.class);
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
    public String upload(HttpServletRequest request, MultipartFile uploadFile) throws IOException {
        if(!uploadFile.isEmpty()) {
            long size=uploadFile.getSize();
            String ff=uploadFile.getName();
            String contentType=uploadFile.getContentType();
            String path = WebUtils.getRealPath(request.getServletContext(), "jj");
            String filename = uploadFile.getOriginalFilename();
            Assert.notNull(filename, "文件名称不能为空!!");
            File file = new File(path,filename);
            String filePath=file.getPath();
            String parentPath=file.getParent();
            String absolutePath=file.getAbsolutePath();
            File parentFile=file.getParentFile();
            String parentFilePath=parentFile.getPath();
            logger.info("path={},filename={},filePath={},parentPath={},absolutePath={},parentFilePath={}",path,filename,filePath,parentPath,absolutePath,parentFilePath);
            boolean resultOfMkdir = false;
            if(!parentFile.exists()) resultOfMkdir=parentFile.mkdir();
            if(resultOfMkdir) {
                uploadFile.transferTo(new File(path + File.separator + filename));
                return "success";
            }
        }
        return "fail";
    }
}
