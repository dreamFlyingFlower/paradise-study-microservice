package com.wy.strategy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认策略注解
 * 
 * @author 飞花梦影
 * @date 2024-05-16 14:02:35
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultStrategy {

}