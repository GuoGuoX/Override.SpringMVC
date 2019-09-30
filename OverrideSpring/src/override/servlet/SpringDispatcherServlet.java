package override.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import override.annotation.SpringController;
import override.annotation.SpringQualifier;
import override.annotation.SpringRequestMapping;
import override.annotation.SpringService;
import override.controller.SpringMVCController;
import override.handlerAdapter.HandlerAdapter;

public class SpringDispatcherServlet extends HttpServlet{
	List<String> classNames = new ArrayList<String>();
	
	//实例集合
	private Map<String,Object> beans = new HashMap<String,Object>();
	
	//映射集合
	Map<String, Object> handlerMap = new HashMap<String, Object>();
	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("init初始化。。。。。。");
		//1、扫描目标类文件
		packageScan("override");
		//2、实例化所有类，并放入map集合
		instance();
		for (Map.Entry<String, Object> bean : beans.entrySet()) {
			System.out.println("map集合"+bean.getKey()+":"+bean.getValue());
		}
		
		// 3、依赖注入，把service层的实例注入到controller
		ioc();
		
		//4、设置映射关系
		handlerAdapter();
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//OverrideSpring/poo
		String requestURI = req.getRequestURI();
		//http://127.0.0.1:8080/OverrideSpring/poo
		StringBuffer requestURL = req.getRequestURL();
		String contextPath = req.getContextPath();
		String path = requestURI.replace(contextPath, "");
		//从映射集合拿到uri对应的方法
		Method method = (Method) handlerMap.get(path);
		//通过访问后缀key到map获取controller
		SpringMVCController object = (SpringMVCController)beans.get("/"+path.split("/")[1]);
		HandlerAdapter handlerAdapter = (HandlerAdapter) beans.get("handlerAdapterImpl");
		Object[] args = handlerAdapter.ha(req,resp,method,beans);
		try {
			//对方法进行调用
			method.invoke(object, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//获取使用了映射注解的类与方法，并与方法进行关联映射
	private void handlerAdapter() {
		if(beans.entrySet().size()<=0){
			System.out.println("没有映射");
			return;
		}
		
		for (Map.Entry<String,Object> bean : beans.entrySet()) {
			 Object value = bean.getValue();
			 Class<? extends Object> class1 = value.getClass();
			if(class1.isAnnotationPresent(SpringController.class)){
				SpringRequestMapping annotation = class1.getAnnotation(SpringRequestMapping.class);
				String controllerPath = annotation.value();
				Method[] methods = class1.getMethods();
				for (Method method : methods) {
					if(method.isAnnotationPresent(SpringRequestMapping.class)){
						SpringRequestMapping annotationMethod = method.getAnnotation(SpringRequestMapping.class);
						String methodPath = annotationMethod.value();
						handlerMap.put(controllerPath+methodPath, method);
					}else{
						continue;
					}
				}
			}
		}
		
	}
	
	//将有依赖关系的对象进行赋值初始化
	private void ioc() {
		if(beans.entrySet().size()<=0){
			System.out.println("没有类可实例化");
			return ;
		}
		//遍历beans
		for (Map.Entry<String, Object> bean : beans.entrySet()) {
			Object value = bean.getValue();
			//获取bean的类型，进而判断是否需要属性注入
			Class<? extends Object> clazz = value.getClass();
			if(clazz.isAnnotationPresent(SpringController.class)){
				//获取所有变量字段
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					//判断是否添加了指定注解
					if(field.isAnnotationPresent(SpringQualifier.class)){
						SpringQualifier annotation = field.getAnnotation(SpringQualifier.class);
						String fieldValue = annotation.value();
						//开启强制注入
						field.setAccessible(true);
						try {
							//为字段赋值
							field.set(value, beans.get(fieldValue));
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else{
						continue;
					}
				}
			}else{
				continue;
			}
		}
		
		
	}
	
	//实例化所有的controller和service层
	private void instance() {
		for (String string : classNames) {
			string = string.replace(".class", "");
			System.out.println(string);
			try {
				Class<?> forName = Class.forName(string);
				if(forName.isAnnotationPresent(SpringController.class)){
					SpringRequestMapping requestMapping = forName.getAnnotation(SpringRequestMapping.class);
					String value = requestMapping.value();
					
					
					//通过反射实例化所有类
					Object newInstance = forName.newInstance();
					//放入map集合中（singleObjects）
					beans.put(value, newInstance);
					
				}else if(forName.isAnnotationPresent(SpringService.class)){
					//获取当前clazz类的注解(通过这个注解可得到当前service的id)  @com.enjoy.james.annotation.EnjoyService(value=JamesServiceImpl)
					SpringService service = (SpringService) forName.getAnnotation(SpringService.class);
					
					//通过反射实例化所有类
					Object newInstance = forName.newInstance();
					//放入map集合中（singleObjects）
					beans.put(service.value(), newInstance);
					
				}
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//扫描指定路径下的所有字节码文件
	private void packageScan(String basePackage) {
		URL resource = this.getClass().getClassLoader().getResource("/"+replaceTo(basePackage));
		String fileResource = resource.getFile();
		fileResource = fileResource.replaceAll("%20", " ");
		File file = new File(fileResource);
		///G:/Workspaces/MyEclipse%202017%20CI/.metadata/.me_tcat85/webapps/OverrideSpring/WEB-INF/classes/override/
		//获取路径下的所有文件和目录
		String[] list = file.list();
		for (String string : list) {
			
			File targetFile = new File(fileResource+string);
			if(targetFile.isDirectory()){
				packageScan(basePackage+"."+string);
			}else{
				classNames.add(basePackage+"."+targetFile.getName());
			}
		}
		
	}
	private String replaceTo(String basePackage) {
		
		return basePackage.replaceAll("\\.", "/");
	}
}
