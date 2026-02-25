package com.lsj.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserF1Controller {

	@GetMapping("/usr/f1/driverchampionship")
	public String showDriverChampionshipPage() {
		return "usr/f1/driverchampionship";
	}
}