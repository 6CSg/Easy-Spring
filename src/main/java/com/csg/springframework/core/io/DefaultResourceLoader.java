package com.csg.springframework.core.io;

import cn.hutool.core.lang.Assert;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            // 从类路径中获取资源
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        } else {
            try {
                // 从网络中获取资源
                URL url = null;
                url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                // 通过读取文件的方式获取资源
                return new FileSystemResource(location);
            }
        }
    }
}
