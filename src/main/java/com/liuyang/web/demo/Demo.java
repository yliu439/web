package com.liuyang.web.demo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Demo {

    private Long id;
    private String name;
    private String alias;
    private LocalDateTime birthday;
}
