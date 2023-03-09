package com.csg.springframework.test.converter;

import com.csg.springframework.core.convert.converter.Converter;
import com.csg.springframework.exception.BeanException;
import com.csg.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class String2LocalDateConverter implements Converter<String, LocalDate> {

    private final DateTimeFormatter DATE_TIME_FORMATTER;

    public String2LocalDateConverter(String pattern) {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public LocalDate convert(String source) throws BeanException {
        return LocalDate.parse(source, DATE_TIME_FORMATTER);
    }
}
