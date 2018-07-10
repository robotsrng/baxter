package com.shattered.baxt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

	@RequestMapping("/")
	public String showIndex() {
		return "index" ;
	}
	
	@RequestMapping("/upload")
	public String showUploadPage() {
		return "upload-video" ;
	}
	
	
	
}
