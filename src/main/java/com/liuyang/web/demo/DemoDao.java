package com.liuyang.web.demo;

import com.liuyang.web.core.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DemoDao extends BaseDao {
    final Logger logger = LoggerFactory.getLogger(DemoDao.class);

    public DemoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    int insert(Demo demo){
        String sql="insert into demo_user(ID,C_NAME,C_ALIAS,C_BIRTHDAY)values (SEQ_DEMO_USER.nextval,?,?,?)";
        return jdbcTemplate.update(sql,demo.getName(),demo.getAlias(),demo.getBirthday());
    }

    List<Demo> selectAll(){
        String sql="select id,c_name as name,c_alias as alias,c_birthday as birthday from demo_user order by id";
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Demo.class));
    }
}
