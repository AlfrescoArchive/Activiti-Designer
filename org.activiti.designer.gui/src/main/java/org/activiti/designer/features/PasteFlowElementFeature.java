/**
 * 
 */
package org.activiti.designer.features;

import org.activiti.designer.bpmn2.model.FlowElement;
import org.activiti.designer.util.CloneUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

/**
 * Paste feature for flow elements.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public class PasteFlowElementFeature extends AbstractPasteFeature {

  public static final int PASTE_OFFSET = 25;

  public PasteFlowElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  public boolean canPaste(IPasteContext context) {
    // only support pasting directly in the diagram (nothing else selected)
    PictogramElement[] pes = context.getPictogramElements();

    if (pes.length != 1 || !isPasteableContext(pes)) {
      return false;
    }

    // can paste, if all objects on the clipboard are Flow elements
    Object[] fromClipboard = getFromClipboard();
    if (fromClipboard == null || fromClipboard.length == 0) {
      return false;
    }
    for (Object object : fromClipboard) {
      if (!(object instanceof FlowElement)) {
        return false;
      }
    }
    return true;
  }

  private boolean isPasteableContext(PictogramElement[] pes) {
    return (pes[0] instanceof Diagram) || (pes[0] instanceof ContainerShape);
  }

  public void paste(IPasteContext context) {
    // we already verified, that we paste directly in the diagram
    PictogramElement[] pes = context.getPictogramElements();

    Diagram diagram = null;
    if (pes[0] instanceof Diagram) {
      diagram = (Diagram) pes[0];
    } else if (pes[0] instanceof ContainerShape) {
      ContainerShape shape = (ContainerShape) pes[0];
      diagram = (Diagram) shape.eContainer();
    }

    // get the FlowElement from the clipboard without copying them
    // (only copy the pictogram element, not the business object)
    // then create new pictogram elements using the add feature
    Object[] objects = getFromClipboard();
    for (Object object : objects) {
      AddContext ac = new AddContext();

      FlowElement clone = CloneUtil.clone((FlowElement) object, diagram);

      ac.setLocation(CopyFlowElementFeature.copyX + PASTE_OFFSET, CopyFlowElementFeature.copyY + PASTE_OFFSET);
      ac.setTargetContainer(diagram);
      addGraphicalRepresentation(ac, clone);
    }
  }
}
