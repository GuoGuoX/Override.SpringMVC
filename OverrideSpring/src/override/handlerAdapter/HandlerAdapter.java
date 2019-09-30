package override.handlerAdapter;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
	
	
	//对方法中的参数进行解析
	public Object[] ha(HttpServletRequest req, HttpServletResponse resp, Method method, Map<String, Object> beans);
	

}
