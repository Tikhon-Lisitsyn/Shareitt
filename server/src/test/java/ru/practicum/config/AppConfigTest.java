package ru.practicum.config;

import org.modelmapper.ModelMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AppConfigTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void testModelMapperBean() {
        assertNotNull(modelMapper, "ModelMapper bean should be initialized");
    }
}
