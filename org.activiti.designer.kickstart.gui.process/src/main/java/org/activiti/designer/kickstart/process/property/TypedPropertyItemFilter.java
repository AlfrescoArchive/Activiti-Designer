package org.activiti.designer.kickstart.process.property;

import org.activiti.designer.kickstart.process.property.PropertyItemBrowser.PropertyItemFilter;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;

public class TypedPropertyItemFilter implements PropertyItemFilter {
  
  protected Class<?> requiredClass;
  
  public TypedPropertyItemFilter(Class<?> requiredClass) {
    this.requiredClass = requiredClass;
  }

  @Override
  public boolean propertySelectable(FormPropertyDefinition definition) {
    if(definition != null) {
      return definition.getClass().equals(requiredClass);
    }
    return false;
  }

}
