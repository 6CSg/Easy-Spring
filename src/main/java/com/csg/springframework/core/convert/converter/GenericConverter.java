package com.csg.springframework.core.convert.converter;

import cn.hutool.core.lang.Assert;

import java.util.Set;

/**
 * Generic:通用的，泛型的
 *
 * GenericConverter是多对多类型的转换器，用户提供sourceObj, sourceType、targetType，
 * 然后从GenericConvertService从多个converter中找到合适的converter对source进行转换
 *
 * 包含了ConvertiblePair内部类，并提供了获取Set<ConvertiblePair>的方法以及定义了convert()抽象方法
 * convert()抽象方法由具体的Converter和ConverterFactory实现
 */
public interface GenericConverter {
    /**
     * 返回当前converter能够转化的target和source，成对装入set返回
     */
    Set<ConvertiblePair> getConvertibleTypes();

    Object convert(Object source, Class<?> sourceType, Class<?> targetType);

    final class ConvertiblePair {
        private final Class<?> sourceType;

        private final Class<?> targetType;

        public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
            Assert.notNull(sourceType, "Source type must not be null");
            Assert.notNull(targetType, "Target type must not be null");
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        public Class<?> getSourceType() {
            return sourceType;
        }

        public Class<?> getTargetType() {
            return targetType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (null == obj || obj.getClass() != ConvertiblePair.class) {
                return false;
            }
            ConvertiblePair other = (ConvertiblePair)obj;
            // 如果两个ConvertiblePair的目标类型和源类型相等，则返回true
            return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);
        }
        // 重写hashCode的原因就是为了当sourceType和targetType相同的两个不相同的ConvertiblePair对象的hashcode()相同，
        // 这样在他们就可被看作converters中的同一个key
        @Override
        public int hashCode() {
            return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
        }
    }
}
