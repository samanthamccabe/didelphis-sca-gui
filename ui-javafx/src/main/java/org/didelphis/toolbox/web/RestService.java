/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.toolbox.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 9/23/2016
 */
@Controller
//@RequestMapping("/") // use "/" because the servlet is already at "/rest"
public class RestService{

	// TODO: Could be a bean
//	private RequestController controller = new RequestController();

	@RequestMapping(
			value = "/open",
			method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE
	)
	@ResponseBody
	public String open(@RequestParam("path") String path) {
//		return controller.open(path);
		return ""; // TODO:
	}
	
	@RequestMapping(
			value = "/save",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_PLAIN_VALUE
	)
	@ResponseBody
	public String save(Object request) {
		
		
		// TODO:
		return "save";
	}
}
