package org.didelphis.toolbox.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 9/23/2016
 */
@Controller
//@RequestMapping("/") // use "/" because the servlet is already at "/rest"
public class RestService{
	
	@RequestMapping(
			value = "/test",
			method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE
	)
	@ResponseBody
	public String test() {
		return "REST Test";
	}
}
