package com.wy.fallback;

import com.wy.feign.FeignUserService;

/**
 * 作为FeignClient注解的fallbackFactory属性传入的参数接口
 * 
 * @author 飞花梦影
 * @date 2021-09-22 23:42:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface FeignUserServiceFactory extends FeignUserService {

}