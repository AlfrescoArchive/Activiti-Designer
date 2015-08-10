package org.activiti.designer.property;

import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.Signal;
import org.activiti.bpmn.model.SignalEventDefinition;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

public class SignalPropertyUtil {
	
  public static String[] fillSignalCombo(Combo signalCombo, SelectionListener selectionListener, Diagram diagram) {
    signalCombo.removeSelectionListener(selectionListener);
    BpmnMemoryModel memoryModel = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    BpmnModel model = memoryModel.getBpmnModel();
    String[] signalArray = new String[model.getSignals().size()];
    List<Signal> signals = (List<Signal>) model.getSignals();
    for (int i = 0; i < signals.size(); i++) {
      Signal signal = signals.get(i);
      signalArray[i] = signal.getName() + " (" + signal.getId() + ")";
    }
    signalCombo.setItems(signalArray);
    signalCombo.select(0);
    signalCombo.addSelectionListener(selectionListener);
    return signalArray;
  }


  public static String getSignalValue(final Event event, final Diagram diagram, final IDiagramContainer diagramContainer) {
    if (event.getEventDefinitions().get(0) != null) {
      final SignalEventDefinition signalDefinition = (SignalEventDefinition) event.getEventDefinitions().get(0);
      final BpmnMemoryModel memoryModel = ModelHandler.getModel(EcoreUtil.getURI(diagram));
      final BpmnModel model = memoryModel.getBpmnModel();
      if (StringUtils.isNotEmpty(signalDefinition.getSignalRef())) {
        for (Signal signal : model.getSignals()) {
          if (signal.getId() != null && signal.getId().equals(signalDefinition.getSignalRef())) {
            return signal.getName() + " (" + signal.getId() + ")";
          }
        }
        
      } else {
        if (model.getSignals().size() > 0) {
          final Runnable runnable = new Runnable() {
            public void run() {
              Signal signal = model.getSignals().iterator().next();
              signalDefinition.setSignalRef(signal.getId());
            }
          };
          
          TransactionalEditingDomain editingDomain = diagramContainer.getDiagramBehavior().getEditingDomain();
          ActivitiUiUtil.runModelChange(runnable, editingDomain, "Model Update");
        }
      }
    }
    return null;
  }

  public static void storeSignalValue(Combo signalCombo, Event event, Diagram diagram) {
    BpmnMemoryModel memoryModel = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    BpmnModel model = memoryModel.getBpmnModel();
    SignalEventDefinition signalDefinition = (SignalEventDefinition) event.getEventDefinitions().get(0);
    signalDefinition.setSignalRef(((List<Signal>) model.getSignals()).get(signalCombo.getSelectionIndex()).getId());
  }
}
