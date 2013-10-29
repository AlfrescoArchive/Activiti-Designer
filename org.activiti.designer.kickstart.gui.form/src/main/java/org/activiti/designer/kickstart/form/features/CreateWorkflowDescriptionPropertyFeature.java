package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateWorkflowDescriptionPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "package-items";

  public CreateWorkflowDescriptionPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Workflow description", "A reference to the workflow description");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    ReferencePropertyDefinition definition = new ReferencePropertyDefinition();
    definition.setType(AlfrescoConversionConstants.FORM_REFERENCE_WORKFLOW_DESCRIPTION);
    definition.setName("Message");
    definition.setWritable(true);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_WORKFLOW_DESCRIPTION.getImageKey();
  }
}
