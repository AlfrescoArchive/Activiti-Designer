package org.activiti.designer.kickstart.eclipse.editor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.swt.widgets.Display;


public class KickstartProcessChangeListener implements ResourceSetListener {

  private final IDiagramEditor diagramEditor;

  public KickstartProcessChangeListener(final IDiagramEditor diagramEditor) {
    super();

    this.diagramEditor = diagramEditor;
  }

  @Override
  public NotificationFilter getFilter() {
    return NotificationFilter.NOT_TOUCH;
  }

  @Override
  public boolean isAggregatePrecommitListener() {
    return false;
  }

  @Override
  public boolean isPostcommitOnly() {
    return false;
  }

  @Override
  public boolean isPrecommitOnly() {
    return false;
  }

  @Override
  public void resourceSetChanged(ResourceSetChangeEvent event) {
    final IDiagramTypeProvider provider = diagramEditor.getDiagramTypeProvider();
    final Diagram diagram = provider.getDiagram();

    if (diagram != null && diagram.getPictogramLinks().isEmpty()) {
      return;
    }

    final Set<EObject> changedBOs = new HashSet<EObject>();
    final List<Notification> notifications = event.getNotifications();

    for (final Notification notification : notifications) {
      final Object notifier = notification.getNotifier();

      if (notifier instanceof EObject) {
        final EObject eNotifier = (EObject) notifier;

        changedBOs.add(eNotifier);
      }
    }

    final INotificationService notificationService = provider.getNotificationService();
    final PictogramElement[] dirtyPEs
      = notificationService.calculateRelatedPictogramElements(changedBOs.toArray());

    if (dirtyPEs.length > 0) {
      // do an asynchronous update in the UI thread
      Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run() {
          IDiagramEditor diagramEditor = provider.getDiagramEditor();
          if (provider.isAutoUpdateAtRuntime() && diagramEditor.isDirty()) {
            notificationService.updatePictogramElements(dirtyPEs);
          } else {
            diagramEditor.refresh();
          }
        }

      });
    }
  }

  @Override
  public Command transactionAboutToCommit(ResourceSetChangeEvent event) throws RollbackException {
    return null;
  }

}
