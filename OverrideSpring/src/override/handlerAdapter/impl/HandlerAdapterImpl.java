package override.handlerAdapter.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import override.Resolver.ArgumentResolver;
import override.annotation.SpringService;
import override.handlerAdapter.HandlerAdapter;

@SpringService("handlerAdapterImpl")
public class HandlerAdapterImpl implements HandlerAdapter{

	

	@Override
	public Object[] ha(HttpServletRequest req, HttpServletResponse resp, Method method, Map<String, Object> beans) {
		//拿到当前方法的参数有哪些
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] params = new Object[parameterTypes.length];
		
		//获取所有实现了ArgumentResolver接口的实现类
		Map<String, Object> beansResolver = getBeansResolver(beans,ArgumentResolver.class);
		int paramIndex = 0;
		int index = 0;
		//对每个参数进行处理,每个参数有对应的参数解析类
		for(Class<?> param : parameterTypes){
			//遍历哪个参数用了哪个解析类
			for(Map.Entry<String, Object> resolver:beansResolver.entrySet()){
				ArgumentResolver argumentResolver = (ArgumentResolver)resolver.getValue();
				boolean support = argumentResolver.support(param, paramIndex, method);
				//当参数注解与解析类对应时,执行参数解析操作
				if(support){
					params[index++] = argumentResolver.paramResolver(req, resp, param, paramIndex, method);
				}else{
					continue;
				}
				
			}
			paramIndex++;
		}
		
		
		return params;
	}
	public Map<String,Object> getBeansResolver(Map<String,Object> beans,Class ArgumentResolver){
		Map<String,Object> result = new HashMap<String,Object>();
		//获取所有实现了ArgumentResolver接口的实现类
		for (Map.Entry<String, Object> bean : beans.entrySet()) {
			Class<?>[] interfaces = bean.getValue().getClass().getInterfaces();
			
			if(interfaces!=null&&interfaces.length>0){
				
				for (Class<?> class1 : interfaces) {
				if(class1.isAssignableFrom(ArgumentResolver)){
					result.put(bean.getKey(), bean.getValue());
					}
				}
			}
		}
		return result;
	}
}
