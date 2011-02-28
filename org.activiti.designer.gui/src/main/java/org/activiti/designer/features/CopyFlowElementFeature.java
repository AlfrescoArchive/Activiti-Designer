/**
 * 
 */
package org.activiti.designer.features;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

/**
 * Copy feature for flow elements.
 * 
 * @author Tiese Barrell
 * @since 0.6.1
 * @version 1
 * 
 */
public class CopyFlowElementFeature extends AbstractCopyFeature {

  public static int copyX = 0;
  public static int copyY = 0;

  public CopyFlowElementFeature(IFeatureProvider fp) {
    super(fp);
  }

  public boolean canCopy(ICopyContext context) {
    final PictogramElement[] pes = context.getPictogramElements();
    if (pes == null || pes.length == 0) { // nothing selected
      return false;
    }

    // return true, if all selected elements are a EClasses
    for (PictogramElement pe : pes) {

      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (!(bo instanceof FlowElement)) {
        return false;
      }
    }
    return true;
  }

  public void copy(ICopyContext context) {

    copyX = Integer.MAX_VALUE;
    copyY = Integer.MAX_VALUE;

    // get the business objects for all pictogram elements
    // we already verified that all business objects are FlowElements
    PictogramElement[] pes = context.getPictogramElements();
    Object[] bos = new Object[pes.length];
    for (int i = 0; i < pes.length; i++) {

      PictogramElement pe = pes[i];

      copyX = Math.min(copyX, pe.getGraphicsAlgorithm().getX());
      copyY = Math.min(copyY, pe.getGraphicsAlgorithm().getY());

      bos[i] = getBusinessObjectForPictogramElement(pe);
    }
    // put all business objects to the clipboard
    putToClipboard(bos);
  }

}
