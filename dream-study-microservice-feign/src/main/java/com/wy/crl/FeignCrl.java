package com.wy.crl;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wy.feign.FeignService;

/**
 * 通用接口,前缀由各个继承本类的子类填写
 * 
 * @author 飞花梦影
 * @date 2021-01-06 16:54:31
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public abstract class FeignCrl {

	public abstract FeignService getService();

	@PostMapping("create")
	public Object create(@RequestBody Map<String, Object> entity) {
		return getService().create(entity);
	}

	@DeleteMapping("remove/{id}")
	Object remove(@PathVariable("id") String id) {
		return getService().remove(id);
	}

	@PostMapping("removes")
	Object removes(@RequestBody List<String> ids) {
		return getService().removes(ids);
	}

	@PutMapping("edit")
	Object edit(@RequestBody Map<String, Object> entity) {
		return getService().edit(entity);
	}

	@GetMapping("getById/{id}")
	Object getById(@PathVariable("id") String id) {
		return getService().getById(id);
	}

	@PostMapping("getList")
	Object getList(@RequestBody Map<String, Object> page) {
		return getService().getList(page);
	}
}