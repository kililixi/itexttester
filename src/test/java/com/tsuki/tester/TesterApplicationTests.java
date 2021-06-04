package com.tsuki.tester;

import com.startsi.redis.tool.RedisUtil;
import com.startsi.redis.tool.TestUtil;
import com.startsi.redis.tool.abc.AbcUtil;
import com.startsi.redis.tool.def.EdfUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TesterApplicationTests {

	@Autowired
	RedisUtil redisUtil;
//
	@Autowired
	TestUtil testUtil;

	@Autowired
	AbcUtil abcUtil;

	@Autowired
	EdfUtil edfUtil;

	@Test
	void contextLoads() {
		System.out.println(redisUtil);
		System.out.println(testUtil);
		System.out.println(abcUtil);
		System.out.println(edfUtil);
		System.out.println(testUtil.getFactory());
	}

}
