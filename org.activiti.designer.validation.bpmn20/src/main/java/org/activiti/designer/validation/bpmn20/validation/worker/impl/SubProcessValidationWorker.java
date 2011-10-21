package org.activiti.designer.validation.bpmn20.validation.worker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.impl.SubProcessImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Validates process according to {@link ValidationCode#VAL_001}.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class SubProcessValidationWorker implements ProcessValidationWorker {

  @Override
  public Collection<ProcessValidationWorkerMarker> validate(final Diagram diagram, final Map<String, List<EObject>> processNodes) {

    final Collection<ProcessValidationWorkerMarker> result = new ArrayList<ProcessValidationWorkerMarker>();

    final List<EObject> subProcesses = processNodes.get(SubProcessImpl.class.getCanonicalName());

    if (subProcesses != null && !subProcesses.isEmpty()) {
      for (final EObject object : subProcesses) {

        final SubProcess subProcess = (SubProcess) object;

        // Do nothing for now

      }
    }

    return result;
  }
}
