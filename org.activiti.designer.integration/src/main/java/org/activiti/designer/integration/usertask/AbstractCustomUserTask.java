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
package org.activiti.designer.integration.usertask;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.activiti.designer.integration.DiagramBaseShape;
import org.activiti.designer.integration.annotation.Property;

/**
 * Abstract base class for implementing CustomUserTasks. Defaults provided by
 * this base class:
 * 
 * @author Tijs Rademakers
 * @since 5.17
 * 
 */
public abstract class AbstractCustomUserTask implements CustomUserTask {

  private static final String DEFAULT_EXTENSIONS_DRAWER_NAME = "Extensions";

  @Override
  public String contributeToPaletteDrawer() {
    return DEFAULT_EXTENSIONS_DRAWER_NAME;
  }

  @Override
  public final String getId() {
    return getClass().getCanonicalName();
  }

  public abstract String getName();

  @Override
  public String getDescription() {
    return getName();
  }

  @Override
  public String getSmallIconPath() {
    return null;
  }

  @Override
  public String getLargeIconPath() {
    return getSmallIconPath();
  }

  @Override
  public String getShapeIconPath() {
    return getSmallIconPath();
  }

  @Override
  public DiagramBaseShape getDiagramBaseShape() {
    return DiagramBaseShape.ACTIVITY;
  }

  @Override
  public Integer getOrder() {
    return 1;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    final Class clazz = this.getClass();
    final Field[] fields = clazz.getDeclaredFields();

    builder.append("Custom User Task ").append(this.getClass().getSimpleName()).append("\n\tID:\t").append(this.getId()).append("\n\tProvider class:\t")
            .append(this.getClass().getCanonicalName()).append("\n\tPalette drawer:\t").append(contributeToPaletteDrawer()).append("\n\tProperties:\n");

    for (final Field field : fields) {
      final Annotation[] annotations = field.getAnnotations();
      for (final Annotation annotation : annotations) {
        if (annotation instanceof Property) {
          builder.append("\t\t").append(field.getName()).append(" (").append(((Property) annotation).type().name()).append(")\n");
        }
      }
    }

    boolean hierarchyOpen = true;
    Class currentClass = clazz;
    while (hierarchyOpen) {
      currentClass = currentClass.getSuperclass();
      if (CustomUserTask.class.isAssignableFrom(currentClass)) {
        for (Field currentSuperclassField : currentClass.getDeclaredFields()) {
          final Annotation[] currentSuperclassFieldAnnotations = currentSuperclassField.getAnnotations();
          for (final Annotation currentSuperclassFieldAnnotation : currentSuperclassFieldAnnotations) {
            if (currentSuperclassFieldAnnotation instanceof Property) {
              builder.append("\t\t").append(currentSuperclassField.getName()).append(" (").append(((Property) currentSuperclassFieldAnnotation).type().name())
                      .append(") (inherited from ").append(currentClass.getSimpleName()).append(")\n");
            }
          }
        }
      } else {
        hierarchyOpen = false;
      }
    }

    return builder.toString();
  }

}
