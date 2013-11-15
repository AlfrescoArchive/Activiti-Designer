package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.alfresco.step.AlfrescoEmailStepDefinition;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link AlfrescoEmailStepDefinition}
 * objects.
 * 
 * @author Frederik Heremans
 */
public class EmailStepShapeController extends SimpleIconStepShapeController {

  public EmailStepShapeController(KickstartProcessFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof AlfrescoEmailStepDefinition;
  }

  @Override
  protected KickstartProcessPluginImage getIcon() {
    return KickstartProcessPluginImage.EMAIL_STEP_ICON;
  }
}
