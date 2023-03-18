package com.csg.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @author VictorG
 * @date 2023/3/18 21:50
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Resource {
    String name();

}
