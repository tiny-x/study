package com.xy.spring.bean.post.process.Bean;

import com.xy.spring.bean.post.process.ImportBeanRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ImportBeanRegister.class)
public @interface EnableDataSource {

}
