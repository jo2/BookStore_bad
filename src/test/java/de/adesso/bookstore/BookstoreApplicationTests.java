package de.adesso.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dummy")
@SpringBootTest
public class BookstoreApplicationTests {

    @Test
    public void contextLoads() {
   }
}
