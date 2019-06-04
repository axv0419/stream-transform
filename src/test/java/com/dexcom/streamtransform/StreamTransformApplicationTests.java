package com.dexcom.streamtransform;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StreamTransformApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(StreamTransformApplicationTests.class);

	@Test
	public void contextLoads() {
		logger.info("Mock Test Case .. Succeeded");
	}

}
