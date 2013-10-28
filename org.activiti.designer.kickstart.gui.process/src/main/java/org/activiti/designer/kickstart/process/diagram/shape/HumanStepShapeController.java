package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.definition.HumanStepDefinition;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link HumanStepDefinition} objects.
 *  
 * @author Frederik Heremans
 */
public class HumanStepShapeController extends SimpleIconStepShapeController {
  
  public HumanStepShapeController(KickstartProcessFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof HumanStepDefinition;
  }
 
  @Override
  protected KickstartProcessPluginImage getIcon() {
    return KickstartProcessPluginImage.HUMAN_STEP_ICON;
  }
}
