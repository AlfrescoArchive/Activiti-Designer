package org.activiti.designer.kickstart.process.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class KickstartProcessDiagramTypeProvider extends AbstractDiagramTypeProvider {

  private KickstartProcessNotificationService activitiNotificationService;

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public KickstartProcessDiagramTypeProvider() {
		super();
		setFeatureProvider(new KickstartProcessFeatureProvider(this));
	}

  @Override
  public INotificationService getNotificationService() {
    if (activitiNotificationService == null) {
      activitiNotificationService = new KickstartProcessNotificationService(this);
    }

    return activitiNotificationService;
  }

  @Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new KickstartProcessToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}

  @Override
  public boolean isAutoUpdateAtStartup() {
    return true;
  }


}
