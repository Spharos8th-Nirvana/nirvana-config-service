package com.nirvana.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// 테스트는 실제 config-repo에 접근하지 않도록 기동 시 clone을 끈다.
@SpringBootTest(properties = "spring.cloud.config.server.git.clone-on-start=false")
class ConfigServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
