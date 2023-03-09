package com.csg.springframework.test.converter;

import com.csg.springframework.core.convert.converter.Converter;
import com.csg.springframework.exception.BeanException;

public class String2IntConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String source) throws BeanException {
        return Integer.parseInt(source);
    }
}
