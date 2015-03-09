/**
 * 
 */
package org.activiti.designer.integration.servicetask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author André Macedo
 * @version 1
 * @since 5.16.0
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyItemsDataSource {
	abstract String filename();
}
