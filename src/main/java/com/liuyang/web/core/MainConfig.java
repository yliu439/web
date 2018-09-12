package com.liuyang.web.core;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.spring.stat.BeanTypeAutoProxyCreator;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"com.liuyang.web"},
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Service.class, Repository.class})
        })
public class MainConfig {
    private final Logger logger = LoggerFactory.getLogger(MainConfig.class);

    private final Environment env;

    @Autowired
    public MainConfig(Environment env) {
        this.env = env;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource getDataSource() throws Exception {
        logger.debug("开始初始化数据源......");
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(env.getProperty("datasource.url"));
        druidDataSource.setUsername(env.getProperty("datasource.username"));
        druidDataSource.setPassword(env.getProperty("datasource.password"));
        druidDataSource.setKeepAlive(true);
        druidDataSource.setInitialSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("datasource.initialSize"))));
        druidDataSource.setMinIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("datasource.minIdle"))));
        druidDataSource.setMaxActive(Integer.parseInt(Objects.requireNonNull(env.getProperty("datasource.maxActive"))));
        druidDataSource.setMaxWait(Integer.parseInt(Objects.requireNonNull(env.getProperty("datasource.maxWait"))));
        //druidDataSource.setFilters(env.getProperty("datasource.filters"));
        druidDataSource.setProxyFilters(getFilterList());
        druidDataSource.setUseGlobalDataSourceStat(true);
        return druidDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

//    @Bean(name="statFilter")
////    public StatFilter getStatFilter(){
////        return  new StatFilter();
////    }
////    @Bean(name="slf4jLogFilter")
////    public Slf4jLogFilter getSlf4jLogFilter(){
////        Slf4jLogFilter slf4jLogFilter=new Slf4jLogFilter();
////        slf4jLogFilter.setStatementExecutableSqlLogEnable(true);
////        return  slf4jLogFilter;
////    }

    @Bean(name="filterList")
    public List<Filter>  getFilterList(){
        List<Filter> list=new ArrayList<>();
        StatFilter statFilter=new StatFilter();
        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(5000);
        statFilter.setMergeSql(true);
        list.add(statFilter);
        Slf4jLogFilter slf4jLogFilter=new Slf4jLogFilter();
        slf4jLogFilter.setStatementExecutableSqlLogEnable(true);
        list.add(slf4jLogFilter);
        return list;
    }

    @Bean(name="druidStatInterceptor")
    public DruidStatInterceptor getDruidStatInterceptor(){
        return new DruidStatInterceptor();
    }

    @Bean
    public BeanTypeAutoProxyCreator getBeanTypeAutoProxyCreator(){
        BeanTypeAutoProxyCreator beanTypeAutoProxyCreator=new BeanTypeAutoProxyCreator();
        beanTypeAutoProxyCreator.setTargetBeanType(BaseDao.class);
        beanTypeAutoProxyCreator.setInterceptorNames("druidStatInterceptor");
        return beanTypeAutoProxyCreator;
    }



}
