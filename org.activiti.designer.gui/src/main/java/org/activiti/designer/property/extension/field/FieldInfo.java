/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.property.extension.field;

import java.lang.reflect.Field;

import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;

/**
 * Container object for Fields used in property screens for
 * {@link CustomServiceTask}s.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 */
public class FieldInfo implements Comparable<FieldInfo> {

  private Property propertyAnnotation;
  private Help helpAnnotation;
  private final Field field;

  public FieldInfo(Field field) {
    this.field = field;
    final Property propertyAnnotation = field.getAnnotation(Property.class);
    if (propertyAnnotation != null) {
      this.propertyAnnotation = propertyAnnotation;
    } else {
      throw new IllegalArgumentException(String.format("The provided field '%s' doesn't have a %s annotation", field.getName(),
              Property.class.getCanonicalName()));
    }

    final Help helpAnnotation = field.getAnnotation(Help.class);
    if (helpAnnotation != null) {
      this.helpAnnotation = helpAnnotation;
    }
  }
  public Property getPropertyAnnotation() {
    return propertyAnnotation;
  }

  public Help getHelpAnnotation() {
    return helpAnnotation;
  }

  public String getFieldName() {
    return field.getName();
  }

  public Field getField() {
    return field;
  }

  public int getOrder() {
    return getPropertyAnnotation().order();
  }

  @Override
  public int compareTo(FieldInfo o) {
    return new Integer(this.getPropertyAnnotation().order()).compareTo(new Integer(o.getPropertyAnnotation().order()));
  }

}
