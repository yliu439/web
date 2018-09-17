package com.liuyang.web.ztest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

//@Data
//@NoArgsConstructor
@Slf4j
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,isGetterVisibility =JsonAutoDetect.Visibility.NONE )
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDemo {

    public String alise;
    @Getter@Setter
    private Long id;
    @Getter@Setter
    private String name;
    @Getter@Setter@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime birthday;
    @Getter@Setter
    private List<Wife> wifeList;

    @Getter@Setter
    private String emptyString;
    @Getter@Setter
    private String nullString;

    @Getter
    private String onlyGetter;
    @Setter
    private String onlySetter;
    private String noGetterAndSetter;

    public String getNoProp(){
        return "noProp";
    }
    private void setNoProp(){

    }

}

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
class Wife{
    private Long id;
    private String Name;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime birthday;
}

