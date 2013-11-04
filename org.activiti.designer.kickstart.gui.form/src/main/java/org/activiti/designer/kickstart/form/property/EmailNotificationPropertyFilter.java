package org.activiti.designer.kickstart.form.property;

import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;

/**
 * @author Frederik Heremans
 */
public class EmailNotificationPropertyFilter extends ReferencePropertyDefinitionFilter {

  @Override
  protected String getAcceptedType() {
    return  AlfrescoConversionConstants.FORM_REFERENCE_EMAIL_NOTIFICATION;
  }
}
