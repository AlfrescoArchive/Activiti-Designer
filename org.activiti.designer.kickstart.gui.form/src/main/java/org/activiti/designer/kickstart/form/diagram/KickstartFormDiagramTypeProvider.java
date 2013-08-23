package org.activiti.designer.kickstart.form.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class KickstartFormDiagramTypeProvider extends AbstractDiagramTypeProvider {

  private KickstartFormNotificationService activitiNotificationService;

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public KickstartFormDiagramTypeProvider() {
		super();
		setFeatureProvider(new KickstartFormFeatureProvider(this));
	}

  @Override
  public INotificationService getNotificationService() {
    if (activitiNotificationService == null) {
      activitiNotificationService = new KickstartFormNotificationService(this);
    }

    return activitiNotificationService;
  }

  @Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new KickstartFormToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}

  @Override
  public boolean isAutoUpdateAtStartup() {
    return true;
  }


}
