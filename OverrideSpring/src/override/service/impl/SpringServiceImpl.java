package override.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import override.annotation.SpringService;
import override.service.SpringMVCService;

@SpringService("springServiceImpl")
public class SpringServiceImpl implements SpringMVCService {

	@Override
	public String query(String name,String age) {
		return "name="+name+"#######"+"age="+age;
	}

}
