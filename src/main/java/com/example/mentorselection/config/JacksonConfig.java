package com.example.mentorselection.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class JacksonConfig {
    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    // 直接注入springboot默认基于配置创建的对象，添加配置
    @PostConstruct
    void converter() {
        SimpleModule simpleModule = new SimpleModule()
                .addSerializer(Long.class, ToStringSerializer.instance)
                .addSerializer(Long.TYPE, ToStringSerializer.instance);
        converter.getObjectMapper().registerModule(simpleModule);
    }
}
