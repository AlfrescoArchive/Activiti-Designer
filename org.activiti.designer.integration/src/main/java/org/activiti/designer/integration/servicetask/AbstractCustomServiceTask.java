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
package org.activiti.designer.integration.servicetask;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.activiti.designer.integration.DiagramBaseShape;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.annotation.Runtime;

/**
 * Abstract base class for implementing CustomServiceTasks. Defaults provided by
 * this base class:
 * 
 * <li>
 * Provides an id which is determined by invoking {@link #getClass()
 * .getCanonicalName}.</li> <li>Contribute to a default palette drawer (i.e.,
 * the "Extensions" drawer). Override {@link #contributeToPaletteDrawer()} if
 * you <strong>do</strong> wish to contribute to a drawer with a name of your
 * own.</li> <li>Sets the diagram base shape to
 * {@link DiagramBaseShape#ACTIVITY}. Override {@link #getDiagramBaseShape()} to
 * customize the shape drawn in the diagram.</li> <li>Implement
 * {@link #getLargeIconPath()} so as to return {@link #getSmallIconPath()}.
 * Override if you have a custom large icon.</li> <li>Implement
 * {@link #getShapeIconPath()} so as to return {@link #getSmallIconPath()}.
 * Override if you have a custom icon for use in the shape.</li> <li>Implement
 * {@link #getDescription()} so as to return {@link #getName()} by default.
 * Override to provide a more specific description.</li><li>Provides a default
 * order of 1. Override {@link #getOrder} if you wish to provide explicit
 * ordering of your {@link CustomServiceTask}s.</li><li>
 * Implements the {@link #getDelegateType()} method</li><li>
 * Implements the {@link #getDelegateSpecification()} method</li>
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * 
 */
public abstract class AbstractCustomServiceTask implements CustomServiceTask {

  private static final String DEFAULT_EXTENSIONS_DRAWER_NAME = "Extensions";

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * contributeToPaletteDrawer()
   */
  @Override
  public String contributeToPaletteDrawer() {
    return DEFAULT_EXTENSIONS_DRAWER_NAME;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getId()
   */
  @Override
  public final String getId() {
    return getClass().getCanonicalName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getDelegateType()
   */
  @Override
  public final DelegateType getDelegateType() {
    return getDelegateType(getRuntimeAnnotation());
  }

  private DelegateType getDelegateType(final Runtime annotation) {

    DelegateType result = DelegateType.NONE;

    if (annotation != null) {
      if (isDelegateDefined(annotation.javaDelegateClass())) {
        result = DelegateType.JAVA_DELEGATE_CLASS;
      } else if (isDelegateDefined(annotation.expression())) {
        result = DelegateType.EXPRESSION;
      } else if (isDelegateDefined(annotation.javaDelegateExpression())) {
        result = DelegateType.JAVA_DELEGATE_EXPRESSION;
      }
    }

    return result;
  }

  private boolean isDelegateDefined(final String definition) {
    return definition != null && !definition.isEmpty() && !"".equals(definition);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getDelegateSpecification()
   */
  @Override
  public final String getDelegateSpecification() {

    String result = "";

    final DelegateType delegateType = getDelegateType();

    if (!DelegateType.NONE.equals(delegateType)) {

      Runtime runtimeAnnotation = getRuntimeAnnotation();
      if (runtimeAnnotation != null) {
        switch (delegateType) {
        case JAVA_DELEGATE_CLASS:
          result = runtimeAnnotation.javaDelegateClass();
          break;
        case EXPRESSION:
          result = runtimeAnnotation.expression();
          break;
        case JAVA_DELEGATE_EXPRESSION:
          result = runtimeAnnotation.javaDelegateExpression();
          break;
        }
      }

    }

    return result;
  }

  private Runtime getRuntimeAnnotation() {

    Runtime result = null;

    final Annotation annotation = this.getClass().getAnnotation(Runtime.class);

    if (annotation != null && Runtime.class.isAssignableFrom(annotation.getClass())) {
      result = ((Runtime) annotation);
    }

    return result;
  }

  public abstract String getName();

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getDescription()
   */
  @Override
  public String getDescription() {
    return getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getSmallIconPath()
   */
  @Override
  public String getSmallIconPath() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getLargeIconPath()
   */
  @Override
  public String getLargeIconPath() {
    return getSmallIconPath();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getShapeIconPath()
   */
  @Override
  public String getShapeIconPath() {
    return getSmallIconPath();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.activiti.designer.integration.servicetask.CustomServiceTask#
   * getDiagramBaseShape()
   */
  @Override
  public DiagramBaseShape getDiagramBaseShape() {
    return DiagramBaseShape.ACTIVITY;
  }

  @Override
  public Integer getOrder() {
    return 1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    final Class clazz = this.getClass();
    final Field[] fields = clazz.getDeclaredFields();

    builder.append("Custom Service Task ").append(this.getClass().getSimpleName()).append("\n\tID:\t").append(this.getId()).append("\n\tProvider class:\t")
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
      if (CustomServiceTask.class.isAssignableFrom(currentClass)) {
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
