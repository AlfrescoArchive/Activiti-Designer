package org.activiti.designer.kickstart.process.property;

import org.activiti.designer.kickstart.process.property.PropertyItemBrowser.PropertyItemFilter;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;

/**
 * Filter that only matches concrete properties (not referencing and not fixed).
 * 
 * @author Frederik Heremans
 */
public class ConcretePropertyItemFilter implements PropertyItemFilter {

  @Override
  public boolean propertySelectable(FormPropertyDefinition definition) {
    boolean matches = true;
    if(definition instanceof ReferencePropertyDefinition) {
      String type = ((ReferencePropertyDefinition) definition).getType();
      matches = !AlfrescoConversionConstants.FORM_REFERENCE_FIELD.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_DUEDATE.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_PRIORITY.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_PACKAGE_ITEMS.equals(type) &&
          !AlfrescoConversionConstants.FORM_REFERENCE_WORKFLOW_DESCRIPTION.equals(type);
    }
    return matches;
  }
}
