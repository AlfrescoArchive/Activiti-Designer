package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.definition.DelayStepDefinition;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link DelayStepDefinition}
 * objects.
 * 
 * @author Frederik Heremans
 */
public class DelayStepShapeController extends SimpleIconStepShapeController {

  public DelayStepShapeController(KickstartProcessFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof DelayStepDefinition;
  }

  @Override
  protected KickstartProcessPluginImage getIcon() {
    return KickstartProcessPluginImage.DELAY_STEP_ICON;
  }
}
