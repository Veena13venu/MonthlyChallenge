package com.monthlychallenge.infrastructure.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.YearMonth;

/**
 * Registers:
 * 1. A Spring MVC converter so ?month=2026-08 query params are parsed as YearMonth.
 * 2. Jackson serializer/deserializer so YearMonth appears as "2026-08" in JSON bodies.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ── Spring MVC @RequestParam converter ────────────────────────────────────

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(new StringToYearMonthConverter());
    }

    public static class StringToYearMonthConverter implements Converter<String, YearMonth> {
        @Override
        public YearMonth convert(@NonNull String source) {
            return YearMonth.parse(source.trim());
        }
    }

    // ── Jackson module for JSON bodies ────────────────────────────────────────

    @Bean
    public SimpleModule yearMonthModule() {
        SimpleModule module = new SimpleModule("YearMonthModule");

        module.addSerializer(YearMonth.class, new StdSerializer<>(YearMonth.class) {
            @Override
            public void serialize(YearMonth value, JsonGenerator gen, SerializerProvider provider)
                    throws IOException {
                gen.writeString(value.toString()); // "2026-08"
            }
        });

        module.addDeserializer(YearMonth.class, new StdDeserializer<>(YearMonth.class) {
            @Override
            public YearMonth deserialize(JsonParser p, DeserializationContext ctx)
                    throws IOException {
                return YearMonth.parse(p.getValueAsString());
            }
        });

        return module;
    }
}
