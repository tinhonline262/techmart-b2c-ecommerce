package com.shopping.microservices.identity_service;

import com.shopping.microservices.identity_service.constant.ProfileConstant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(ProfileConstant.TEST)
class SpringBootTemplateApplicationTests {

	@Test
	void contextLoads() {
	}

}
