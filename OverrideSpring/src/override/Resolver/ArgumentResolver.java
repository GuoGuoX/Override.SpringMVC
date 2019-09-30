package override.Resolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ArgumentResolver {
	//判断传入的参数是否一致
	public boolean support(Class<?> type, int paramIndex, Method method);
	//获取注解的 值,并解析方法中的参数
	public Object paramResolver(HttpServletRequest request,
            HttpServletResponse response, Class<?> type, int paramIndex,
            Method method);
}	
