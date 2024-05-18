package com.maids.LMS.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebMvcSetup implements WebMvcConfigurer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Register JavaTimeModule to properly serialize/deserialize Java 8 Date/Time types
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        objectMapper.registerModule(javaTimeModule);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converters.add(converter);
    }

    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString()); // Customize the serialization format here
        }
    }
}
