package org.activiti.designer.property;

import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

public class AbstractPropertyCustomTaskSection extends BaseActivitiPropertySection {

  public void runModelChange(final Runnable runnable) {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = getBusinessObject(pe);
      if (bo instanceof ServiceTask || bo instanceof UserTask) {
        DiagramEditor diagramEditor = (DiagramEditor) getDiagramEditor();
        TransactionalEditingDomain editingDomain = diagramEditor.getEditingDomain();
        ActivitiUiUtil.runModelChange(runnable, editingDomain, "Model Update");
      }
    }
  }

}