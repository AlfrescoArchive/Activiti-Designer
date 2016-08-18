/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.activiti.designer.integration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.activiti.designer.integration.servicetask.CustomServiceTask;

/**
 * Defines the Runtime attributes for a {@link CustomServiceTask}. In
 * particular, defines the Class that implements JavaDelegation or the
 * expression to be resolved by Activiti during runtime when processing a node
 * defined by the {@link CustomServiceTask} it is placed on.
 * 
 * @author Tiese Barrell
 * @author Michael Priess
 * @version 2
 * @since 0.5.1
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface Runtime {

  /**
   * <p>
   * The fully qualified classname of the class that implements JavaDelegate.
   * You can use {@link Property} annotations to define properties of the
   * {@link CustomServiceTask} in combination with this specification.
   * </p>
   * 
   * <p>
   * This option will result in <code>activiti:class</code> being generated in
   * the process' BPMN output.
   * </p>
   * 
   * @return the classname
   */
  String javaDelegateClass() default "";

  /**
   * <p>
   * The expression to be resolved. You can <strong>not</strong> use
   * {@link Property} annotations to define properties of the
   * {@link CustomServiceTask} in combination with this specification, because
   * the fields will not be injected into the class by Activiti during runtime.
   * If you require an expression but also need properties, use
   * {@link #javaDelegateExpression()} instead.
   * </p>
   * 
   * <p>
   * This option will result in <code>activiti:expression</code> being generated
   * in the process' BPMN output.
   * </p>
   * 
   * @see #javaDelegateExpression()
   * 
   * @return the expression
   */
  String expression() default "";

  /**
   * <p>
   * The expression that will resolve to a class that implements JavaDelegate.
   * You can use {@link Property} annotations to define properties of the
   * {@link CustomServiceTask} in combination with this specification.
   * </p>
   * 
   * <p>
   * This option will result in <code>activiti:delegateExpression</code> being
   * generated in the process' BPMN output.
   * </p>
   * 
   * @return the expression
   */
  String javaDelegateExpression() default "";
}