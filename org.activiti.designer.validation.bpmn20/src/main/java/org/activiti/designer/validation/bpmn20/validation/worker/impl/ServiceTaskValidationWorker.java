package org.activiti.designer.validation.bpmn20.validation.worker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.designer.property.extension.util.ExtensionUtil;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.impl.ServiceTaskImpl;
import org.eclipse.bpmn2.impl.SubProcessImpl;
import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Validates process according to {@link ValidationCode#VAL_004}.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class ServiceTaskValidationWorker implements ProcessValidationWorker {

  private static final String NO_CLASS_EXCEPTION_MESSAGE_PATTERN = "ServiceTask '%s' has no class specified";

  @Override
  public Collection<ProcessValidationWorkerMarker> validate(final Diagram diagram, final Map<String, List<EObject>> processNodes) {

    final Collection<ProcessValidationWorkerMarker> result = new ArrayList<ProcessValidationWorkerMarker>();

    final List<EObject> serviceTasks = processNodes.get(ServiceTaskImpl.class.getCanonicalName());

    if (serviceTasks != null && !serviceTasks.isEmpty()) {
      for (final EObject object : serviceTasks) {

        if (!ExtensionUtil.isCustomServiceTask(object)) {

          final ServiceTask serviceTask = (ServiceTask) object;

          if ((serviceTask.getImplementationType() == null || serviceTask.getImplementationType().length() == 0 || "classType".equalsIgnoreCase(serviceTask
                  .getImplementationType())) && serviceTask.getImplementation() == null || serviceTask.getImplementation().length() == 0) {

            result.add(new ProcessValidationWorkerMarker(IMarker.SEVERITY_ERROR, String.format(NO_CLASS_EXCEPTION_MESSAGE_PATTERN, serviceTask.getName()),
                    serviceTask.getId(), ValidationCode.VAL_004));
          }
        }
      }
    }
    
    final List<EObject> subProcesses = processNodes.get(SubProcessImpl.class.getCanonicalName());

    if (subProcesses != null && !subProcesses.isEmpty()) {
      for (final EObject object : subProcesses) {

        final SubProcess subProcess = (SubProcess) object;
        
        final Map<String, List<EObject>> subElementsMap = new HashMap<String, List<EObject>>();

        for (final FlowElement subElement : subProcess.getFlowElements()) {

          String nodeType = subElement.getClass().getCanonicalName();

          if (nodeType != null) {
            if (!subElementsMap.containsKey(nodeType)) {
            	subElementsMap.put(nodeType, new ArrayList<EObject>());
            }
            subElementsMap.get(nodeType).add(subElement);
          }
        }
        
        result.addAll(validate(diagram, subElementsMap));
      }
    }

    return result;
  }
}
