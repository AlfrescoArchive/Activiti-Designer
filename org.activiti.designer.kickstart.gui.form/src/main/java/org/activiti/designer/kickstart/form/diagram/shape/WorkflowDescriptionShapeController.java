package org.activiti.designer.kickstart.form.diagram.shape;

import org.activiti.designer.kickstart.form.KickstartFormPluginImage;
import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ReferencePropertyDefinition;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * A {@link BusinessObjectShapeController} capable of creating and updating shapes for
 * {@link ReferencePropertyDefinition} objects, referencing the workflow description.
 * 
 * @author Frederik Heremans
 */
public class WorkflowDescriptionShapeController extends SimpleIconInputShapeController {

  public WorkflowDescriptionShapeController(KickstartFormFeatureProvider featureProvider) {
    super(featureProvider);
  }

  @Override
  public boolean canControlShapeFor(Object businessObject) {
    return businessObject instanceof ReferencePropertyDefinition && AlfrescoConversionConstants.FORM_REFERENCE_WORKFLOW_DESCRIPTION.equals(
        ((ReferencePropertyDefinition) businessObject).getType());
  }

  @Override
  public GraphicsAlgorithm getGraphicsAlgorithmForDirectEdit(ContainerShape container) {
    // No direct editing supported
    return null;
  }

  @Override
  public boolean isShapeUpdateNeeded(ContainerShape shape, Object businessObject) {
    return false;
  }

  @Override
  protected String getIconKey(FormPropertyDefinition definition) {
    return KickstartFormPluginImage.NEW_WORKFLOW_DESCRIPTION.getImageKey();
  }
}
