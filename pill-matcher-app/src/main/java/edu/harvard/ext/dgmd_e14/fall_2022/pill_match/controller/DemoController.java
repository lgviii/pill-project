package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.controller;


// Importing required classes
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

// Annotation
@Controller
// Main class
@RestController
public class DemoController {
  	@GetMapping("/hello")
	public String testResponse(@RequestParam(value = "myName", defaultValue = "World") String name) {
//		return String.format("Hello %s!", name);
        return "This app is up!";
	}
}