package com.lsj.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ 올바른 import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class flaskdataController {

	@GetMapping("/display")
	public String displayData(Model model) {

		RestTemplate restTemplate = new RestTemplate();
		String flaskUrl = "http://localhost:5000/api/data";

		Map<String, Object> flaskData = restTemplate.getForObject(flaskUrl, Map.class);

		model.addAttribute("message", flaskData.get("message"));
		model.addAttribute("value", flaskData.get("value"));
		model.addAttribute("items", flaskData.get("items"));

		return "usr/f1/dataView";
	}
}