/**
 * 
 */
package org.activiti.designer.kickstart.process.features;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;

/**
 * @author Tiese Barrell
 * @version 2
 * @since 0.5.0
 * 
 */
public abstract class AbstractCreateBPMNConnectionFeature extends AbstractCreateConnectionFeature {

  public AbstractCreateBPMNConnectionFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }

  protected abstract String getFeatureIdKey();

  protected abstract Class<? extends BaseElement> getFeatureClass();

  protected String getNextId() {
    return ActivitiUiUtil.getNextId(getFeatureClass(), getFeatureIdKey(), getDiagram());
  }

}
