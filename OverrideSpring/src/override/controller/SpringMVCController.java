package override.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import override.annotation.SpringController;
import override.annotation.SpringQualifier;
import override.annotation.SpringRequestMapping;
import override.annotation.SpringRequestParam;
import override.service.SpringMVCService;

@SpringController
@SpringRequestMapping("/contro")
public class SpringMVCController {
	
	@SpringQualifier("springServiceImpl")
	private SpringMVCService springMVCService;
	
	@SpringRequestMapping("/query")
	public void query(HttpServletRequest req,HttpServletResponse resp,
			@SpringRequestParam("name") String name,@SpringRequestParam("age") String age
			){
		String query = springMVCService.query(name, age);
		try {
			resp.getWriter().write(query);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
