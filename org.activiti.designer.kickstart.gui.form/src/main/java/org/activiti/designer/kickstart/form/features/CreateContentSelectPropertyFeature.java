package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreateContentSelectPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "people";

  public CreateContentSelectPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Content selection", "Add a content selection field");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    ReferencePropertyDefinition definition = new ReferencePropertyDefinition();
    definition.setName("Select content");
    definition.setWritable(true);
    definition.setType(AlfrescoConversionConstants.CONTENT_TYPE_CONTENT);
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_PACKAGE_ITEMS.getImageKey();
  }
}
