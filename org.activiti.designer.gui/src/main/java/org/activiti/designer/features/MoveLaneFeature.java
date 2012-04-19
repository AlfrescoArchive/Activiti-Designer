package org.activiti.designer.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

public class MoveLaneFeature extends DefaultMoveShapeFeature {

	public MoveLaneFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
  public boolean canMoveShape(IMoveShapeContext context) {
    return false;
  }
}