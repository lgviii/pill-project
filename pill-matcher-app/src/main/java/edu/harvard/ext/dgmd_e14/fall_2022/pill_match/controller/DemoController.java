package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
public class DemoController {
  	@GetMapping("/test")
	public String testResponse(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return "The pill app is up!";
	}
}