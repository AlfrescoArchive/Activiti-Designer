package org.activiti.designer.kickstart.process.diagram.shape;

import org.activiti.designer.kickstart.process.KickstartProcessPluginImage;
import org.activiti.designer.kickstart.process.diagram.KickstartProcessFeatureProvider;
import org.activiti.workflow.simple.definition.ScriptStepDefinition;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for {@link ScriptStepDefinition}
 * objects.
 * 
 * @author Frederik Heremans
 */
public class ScriptStepShapeController extends SimpleIconStepShapeController {

  public ScriptStepShapeController(KickstartProcessFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof ScriptStepDefinition;
  }

  @Override
  protected KickstartProcessPluginImage getIcon() {
    return KickstartProcessPluginImage.SCRIPT_STEP_FEATURE;
  }
}
