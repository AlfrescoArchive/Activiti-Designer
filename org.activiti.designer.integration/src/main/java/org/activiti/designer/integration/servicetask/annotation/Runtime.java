/**
 * 
 */
package org.activiti.designer.integration.servicetask.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.activiti.designer.integration.servicetask.CustomServiceTask;

/**
 * Defines the Runtime attributes for a {@link CustomServiceTask}. In particular, defines the Class that implements
 * JavaDelegation and is instantiated by Activiti during runtime when processing a node defined by the
 * {@link CustomServiceTask} it is placed on.
 * 
 * @author Tiese Barrell
 * @version 1
 * @since 0.5.1
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface Runtime {

	String delegationClass();

}
