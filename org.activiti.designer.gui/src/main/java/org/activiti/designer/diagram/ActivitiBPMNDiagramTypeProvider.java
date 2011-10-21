package org.activiti.designer.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class ActivitiBPMNDiagramTypeProvider extends AbstractDiagramTypeProvider {

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public ActivitiBPMNDiagramTypeProvider() {
		super();
		setFeatureProvider(new ActivitiBPMNFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new ActivitiToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}

}
