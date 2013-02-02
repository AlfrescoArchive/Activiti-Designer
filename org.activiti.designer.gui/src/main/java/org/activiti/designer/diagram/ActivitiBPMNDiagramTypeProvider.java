package org.activiti.designer.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class ActivitiBPMNDiagramTypeProvider extends AbstractDiagramTypeProvider {

  private ActivitiNotificationService activitiNotificationService;

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public ActivitiBPMNDiagramTypeProvider() {
		super();
		setFeatureProvider(new ActivitiBPMNFeatureProvider(this));
	}

  @Override
  public INotificationService getNotificationService() {
    if (activitiNotificationService == null) {
      activitiNotificationService = new ActivitiNotificationService(this);
    }

    return activitiNotificationService;
  }

  @Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new ActivitiToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}

  @Override
  public boolean isAutoUpdateAtStartup() {
    return true;
  }


}
