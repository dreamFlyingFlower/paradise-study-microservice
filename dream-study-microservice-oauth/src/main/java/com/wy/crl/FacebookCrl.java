package com.wy.crl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wy.oauth.facebook.FacebookApiBinding;

/**
 * FacebookAPI
 *
 * @author 飞花梦影
 * @date 2021-07-01 20:26:09
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Controller
@RequestMapping("facebook")
public class FacebookCrl {

	@Autowired
	private FacebookApiBinding faceApiBinding;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("profile", faceApiBinding.getProfile());
		model.addAttribute("feed", faceApiBinding.getFacebookBriefs());
		return "home";
	}
}