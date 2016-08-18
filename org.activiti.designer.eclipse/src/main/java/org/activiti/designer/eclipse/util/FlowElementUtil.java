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
package org.activiti.designer.eclipse.util;

import java.lang.reflect.Method;

import org.activiti.bpmn.model.FlowElement;
import org.apache.commons.lang.StringUtils;

public class FlowElementUtil {

  public interface ComparableField {

    public String getGetter();
  }

  public static enum StringField implements ComparableField {
    NAME("getName"), SCRIPT("getScript"), SCRIPTFORMAT("getScriptFormat"), ASSIGNEE("getAssignee"), IMPLEMENTATION("getImplementation"), IMPLEMENTATION_TYPE(
            "getImplementationType");

    private final String getter;

    StringField(String getter) {
      this.getter = getter;
    }

    public String getGetter() {
      return this.getter;
    }

  }

  public static enum ExtensionField implements ComparableField {
    NAME("getName"), EXPRESSION("getExpression");

    private final String getter;

    ExtensionField(String getter) {
      this.getter = getter;
    }

    public String getGetter() {
      return this.getter;
    }
  }

  public static boolean elementsNeedUpdate(FlowElement source, FlowElement target, ComparableField... properties) {

    boolean isDifferent = false;

    for (ComparableField property : properties) {

      try {

        if (property.getClass() == StringField.class) {

          Method sourceMethod = source.getClass().getMethod(property.getGetter(), null);
          Method targetMethod = target.getClass().getMethod(property.getGetter(), null);

          String sourceValue = (String) sourceMethod.invoke(source);
          String targetValue = (String) targetMethod.invoke(target);

          if (StringUtils.equals(sourceValue, targetValue) == false) {
            isDifferent = true;
            break;
          }

        }
        
        if (property.getClass() == ExtensionField.class) {
          
          Method sourceMethod = source.getClass().getMethod("getActivitiListeners", null);
          Method targetMethod = target.getClass().getMethod("getActivitiListeners", null);

          /*List<ActivitiListener> sourceListeners = (List<ActivitiListener>) sourceMethod.invoke(source);
          List<ActivitiListener> targetListeners = (List<ActivitiListener>) targetMethod.invoke(source);
          
          for (int i = 0; i < sourceListeners.size(); i++) {
            
            ActivitiListener sourceListener = sourceListeners.get(i);
            sourceListener.getImplementation();
            
          }*/
          
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    return isDifferent;
  }

}
