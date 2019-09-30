package override.Resolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import override.annotation.SpringRequestParam;
import override.annotation.SpringService;
@SpringService("springRequestParamResolver")
public class SpringRequestParamResolver implements ArgumentResolver {

	@Override
	public boolean support(Class<?> type, int paramIndex, Method method) {
		//获取方法中的参数类型和参数值,其为二维数组
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Annotation[] annotations = parameterAnnotations[paramIndex];
		for(Annotation anno :annotations){
			if(SpringRequestParam.class.isAssignableFrom(anno.getClass())){
				return true;
			}
		}
		return false;
	}

	
	/*
	 * @RequestMapping("/order")
	 * order(@RequestBody String params, @RequestHeader @RequestParam String param1){//参数有多种类型接收方式
	 * }
	 */
	//参数解析,并获取注解的值
	@Override
	public Object paramResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type,
			int paramIndex, Method method) {
		
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Annotation[] annotations = parameterAnnotations[paramIndex];
		for(Annotation anno :annotations){
			if(SpringRequestParam.class.isAssignableFrom(anno.getClass())){
				SpringRequestParam ar = (SpringRequestParam)anno;
				String value = ar.value();
				return request.getParameter(value);
			}
		}
		
		return null;
	}

}
