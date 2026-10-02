package com.pap.springbootservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest 
class SSEApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test 
    void testSSEApplication() {
        Assertions.assertDoesNotThrow(
                () -> applicationContext.getBean(SSEApplication.class));
    }

}
