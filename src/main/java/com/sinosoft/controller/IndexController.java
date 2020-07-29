package com.sinosoft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
	@RequestMapping(value="/index",method= RequestMethod.GET)
	public ModelAndView index(){
		ModelAndView m=new ModelAndView();
		m.setViewName("index");
		return m;
	} 
	@RequestMapping(value = "/shouye", method = RequestMethod.GET)
	public ModelAndView shouye() {
		ModelAndView m = new ModelAndView();
		m.setViewName("shouye");
		return m;
	}
	@RequestMapping(value="/unauthorized")
	public ModelAndView unauthorized(){
		ModelAndView m=new ModelAndView();
		m.setViewName("/error/unauthorized");
		return m;
	} 
	
}
