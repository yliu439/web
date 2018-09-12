package com.liuyang.web.core;

import com.liuyang.web.demo.DemoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BaseDao {

    final Logger logger = LoggerFactory.getLogger(BaseDao.class);
    protected final JdbcTemplate jdbcTemplate;

    public BaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
