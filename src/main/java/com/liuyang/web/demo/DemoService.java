package com.liuyang.web.demo;

        import org.apache.commons.csv.CSVFormat;
        import org.apache.commons.csv.CSVParser;
        import org.apache.commons.csv.CSVRecord;
        import org.apache.tika.Tika;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.scheduling.annotation.Async;
        import org.springframework.stereotype.Service;
        import org.springframework.util.Assert;
        import org.springframework.util.StringUtils;
        import org.springframework.web.multipart.MultipartFile;
        import org.springframework.web.util.WebUtils;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileReader;
        import java.io.Reader;
        import java.nio.charset.StandardCharsets;
        import java.util.List;
        import java.util.UUID;

@Service
public class DemoService {

    final Logger logger = LoggerFactory.getLogger(DemoService.class);
    private final DemoDao demoDao;

    public DemoService(DemoDao demoDao) {
        this.demoDao = demoDao;
    }

    int add(Demo demo) {
        return demoDao.insert(demo);
    }

    List<Demo> getAll() {
        return demoDao.selectAll();
    }


    String upload(String path, MultipartFile uploadFile) throws Exception {
        long size = uploadFile.getSize();
        String name = uploadFile.getName();//获取的是上传组件的name属性的值
        String contentType = new Tika().detect(uploadFile.getInputStream());
        //String contentType=uploadFile.getContentType();//该方法获取的文件类型不准确，不能判断出修改类型或无类型文件的真实类型
        String originalFilename = uploadFile.getOriginalFilename();
        Assert.notNull(originalFilename, "文件名称不能为空!!");
        File pathFile = new File(path);
        //File parentFile=file.getParentFile();
        boolean result = true;
        if (!pathFile.exists()) result = pathFile.mkdirs();
        if (!result) return "fail";
        File newFile = new File(path + File.separator + UUID.randomUUID().toString());
        uploadFile.transferTo(newFile);

        parseCSV(newFile);
        return "success";
    }

    String parseCSV(File file) throws Exception {
        String contentType = new Tika().detect(new FileInputStream(file));
        logger.info("contentType={}", contentType);
        if (!StringUtils.hasText("text/plain")) return "文件格式不符!";
        Reader in = new FileReader(file.getAbsolutePath());
        //Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("Id", "Name", "Age").parse(in);
        CSVParser parser = CSVParser.parse(file, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
        List<CSVRecord> records = parser.getRecords();
//        records.forEach(record -> logger.info("id={},name={},age={}",record.get("Id"),record.get("Name"),record.get("Age")));
        for (CSVRecord record : records) {
//            logger.info(String.valueOf(((CSVParser) records).getCurrentLineNumber()));
//            logger.info(String.valueOf(((CSVParser) records).getRecordNumber()));
            logger.info(String.valueOf(record.getRecordNumber()));
            logger.info(String.valueOf(record.getComment()));
            logger.info(record.get(1));
            logger.info(record.get(2));
            logger.info(record.get(3));
        }
        return "";
    }


    @Async
    void processAsync(int i) throws Exception {
        logger.info("异步线程{},线程id:{},线程名称:{}",i, Thread.currentThread().getId(), Thread.currentThread().getName());
        Thread.sleep(3000);
    }
}
