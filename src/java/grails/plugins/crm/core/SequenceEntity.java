package grails.plugins.crm.core;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@GroovyASTTransformationClass("grails.plugins.crm.core.SequenceASTTransformation")
public @interface SequenceEntity {
    String property() default "number";
    int maxSize() default 10;
    boolean blank() default false;
    String unique() default "true";
}
