package com.liuyang.web.core;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.spring.stat.BeanTypeAutoProxyCreator;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import net.spy.memcached.*;
import net.spy.memcached.transcoders.SerializingTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
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

    //Redis配置
    @Bean(name="redisConnectionFactory")
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(
                env.getProperty("redis.standalone.server", "127.0.0.1"),
                env.getProperty("redis.standalone.port", Integer.class, 6379)));
    }
    @Bean
    public <K,V> RedisTemplate<K,V> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<K,V> redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        StringRedisSerializer stringRedisSerializer=new StringRedisSerializer();
        //GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer=new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    //Memcached配置使用原生的Client
    @Bean
    public MemcachedClient memcachedClient(SerializingTranscoder serializingTranscoder) throws IOException {
        return new MemcachedClient(new ConnectionFactoryBuilder()
                .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                .setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT)
                .setOpTimeout(1000)
                .setHashAlg(DefaultHashAlgorithm.KETAMA_HASH)
                .setFailureMode(FailureMode.Redistribute)
                .setTimeoutExceptionThreshold(1998)
                .setUseNagleAlgorithm(false)
                .setTranscoder(serializingTranscoder)
                .build(),
                AddrUtil.getAddresses(env.getProperty("spymemcached.servers","127.0.0.1:11211")));
    }
    @Bean
    public SerializingTranscoder serializingTranscoder(){
        SerializingTranscoder serializingTranscoder=new SerializingTranscoder();
        serializingTranscoder.setCompressionThreshold(1024);
        return serializingTranscoder;
    }

    @Bean(name="poolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        logger.debug("配置异步线程池........");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 线程池维护线程的最少数量
        executor.setMaxPoolSize(20); // 线程池维护线程的最大数量
        executor.setKeepAliveSeconds(300); // 空闲线程的最长保留时间,超过此时间空闲线程会被回收
        executor.setQueueCapacity(100); // 线程池所使用的缓冲队列
        executor.setThreadNamePrefix("MAIN-ThreadPool#");
        // rejection-policy：当线程池线程已达到最大值且任务队列也满了的情况下，如何处理新任务
        // CALLER_RUNS：这个策略重试添加当前的任务，他会自动重复调用 execute() 方法，直到成功
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.afterPropertiesSet();//Calls { after the container applied all property values.
        return executor;
    }


}
