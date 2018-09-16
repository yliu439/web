package com.liuyang.web.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
class Demo implements Serializable {

    private Long id;
    private String name;
    private String alias;

    //对于LocalDateTime类型的属性 @JsonFormat注解要配合ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());使用
    //或者配合HttpMessageConverter的Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().modulesToInstall(new JavaTimeModule());使用
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime birthday;
}
