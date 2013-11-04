package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateEmailNotificationPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "email-notification";

  public CreateEmailNotificationPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Email notification", "A reference to the email notification checkbox");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    ReferencePropertyDefinition definition = new ReferencePropertyDefinition();
    definition.setType(AlfrescoConversionConstants.FORM_REFERENCE_EMAIL_NOTIFICATION);
    definition.setName("Send email notification");
    definition.setWritable(true);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_EMAIL_NOTIFICATION.getImageKey();
  }
}
