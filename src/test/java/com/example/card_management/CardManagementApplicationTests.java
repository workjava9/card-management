package com.example.card_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")   // <-- добавить
class CardManagementApplicationTests {
	@Test void contextLoads() {}
}
