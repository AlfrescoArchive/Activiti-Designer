package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateGroupSelectPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "group";

  public CreateGroupSelectPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Group selection", "Add a group selection field");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    ReferencePropertyDefinition definition = new ReferencePropertyDefinition();
    definition.setName("Select group");
    definition.setWritable(true);
    definition.setType(AlfrescoConversionConstants.CONTENT_TYPE_GROUP);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_GROUP_SELECT.getImageKey();
  }
}
