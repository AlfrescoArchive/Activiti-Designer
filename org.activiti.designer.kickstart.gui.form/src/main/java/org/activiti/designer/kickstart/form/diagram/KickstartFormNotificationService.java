package org.activiti.designer.kickstart.form.diagram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.notification.INotificationService;

public class KickstartFormNotificationService implements INotificationService {

  private final IDiagramTypeProvider diagramTypeProvider;

  public KickstartFormNotificationService(final IDiagramTypeProvider diagramTypeProvider) {
    super();

    this.diagramTypeProvider = diagramTypeProvider;
  }

  public IDiagramTypeProvider getDiagramTypeProvider() {
    return diagramTypeProvider;
  }

  @Override
  public PictogramElement[] calculateRelatedPictogramElements(Object[] bos) {
    final List<Object> changedAndRelatedBOs = new ArrayList<Object>();

    for (final Object bo : bos) {
      changedAndRelatedBOs.add(bo);
    }

    final Object[] relatedBOs = diagramTypeProvider.getRelatedBusinessObjects(bos);

    for (final Object bo : relatedBOs) {
      changedAndRelatedBOs.add(bo);
    }

    return calculateLinkedPictogramElements(changedAndRelatedBOs);
  }

  private PictogramElement[] calculateLinkedPictogramElements(List<Object> changedAndRelatedBOs) {
    final List<PictogramElement> result = new ArrayList<PictogramElement>();

    final IFeatureProvider fp = diagramTypeProvider.getFeatureProvider();

    for (final Object bo : changedAndRelatedBOs) {
      final PictogramElement[] pes = fp.getAllPictogramElementsForBusinessObject(bo);

      for (final PictogramElement pe : pes) {
        result.add(pe);
      }
    }

    return result.toArray(new PictogramElement[] {});
  }

  @Override
  public void updatePictogramElements(PictogramElement[] pes) {
    final IFeatureProvider fp = diagramTypeProvider.getFeatureProvider();

    for (PictogramElement pe : pes) {
      final UpdateContext updateContext = new UpdateContext(pe);

      fp.updateIfPossibleAndNeeded(updateContext);
    }
  }

}
