package org.activiti.designer.kickstart.process.property;

import org.activiti.designer.kickstart.process.property.PropertyItemBrowser.PropertyItemFilter;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;

public class UserPropertyItemFilter implements PropertyItemFilter {

  @Override
  public boolean propertySelectable(FormPropertyDefinition definition) {
    boolean matches = false;
    if(definition instanceof ReferencePropertyDefinition) {
      matches = AlfrescoConversionConstants.CONTENT_TYPE_PEOPLE.equals(((ReferencePropertyDefinition) definition).getType());
    }
    return matches;
  }
}
