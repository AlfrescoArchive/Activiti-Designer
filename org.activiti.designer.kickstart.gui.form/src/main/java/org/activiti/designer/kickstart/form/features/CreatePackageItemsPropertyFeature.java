package org.activiti.designer.kickstart.form.features;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.features.context.ICreateContext;

public class CreatePackageItemsPropertyFeature extends AbstractCreateFormPropertyFeature {

  public static final String FEATURE_ID_KEY = "package-items";

  public CreatePackageItemsPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp, "Package items", "A reference to the package-items");
  }

  @Override
  public boolean canCreate(ICreateContext context) {
    return true;
  }
  
  @Override
  protected FormPropertyDefinition createFormPropertyDefinition(ICreateContext context) {
    ReferencePropertyDefinition definition = new ReferencePropertyDefinition();
    definition.setType(AlfrescoConversionConstants.FORM_REFERENCE_PACKAGE_ITEMS);
    definition.setName("Items");
    return definition;
  }

  @Override
  public String getCreateImageId() {
    return KickstartFormPluginImage.NEW_PACKAGE_ITEMS.getImageKey();
  }
}
