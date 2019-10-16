package com.komlancz.idomsoft.test.okmanyellenorzo.konfig;

import com.komlancz.idomsoft.test.okmanyellenorzo.segito.ValidatorBean;
import com.komlancz.idomsoft.test.okmanyellenorzo.segito.ValidatorBeanImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanKonfigurator {

    @Bean
    public ValidatorBean validatorBean(){
        return new ValidatorBeanImpl();
    }
}
