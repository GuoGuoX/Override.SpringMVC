package override.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})//作用：此注解可标注在类或接口上
@Retention(RetentionPolicy.RUNTIME)//作用：此注解的生命周期，可存在于class文件中，可被反射获取到
@Documented//作用：可存在于javadoc中
public @interface SpringQualifier {
	String value() default "";
}
