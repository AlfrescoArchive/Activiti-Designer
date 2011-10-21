package org.activiti.designer.validation.bpmn20.validation.worker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.impl.SequenceFlowImpl;
import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Validates process according to {@link ValidationCode#VAL_005} and
 * {@link ValidationCode#VAL_006}.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class SequenceFlowValidationWorker implements ProcessValidationWorker {

  private static final String NO_SOURCE_ACTIVITY_EXCEPTION_MESSAGE_PATTERN = "SequenceFlow '%s' has no source activity";
  private static final String NO_TARGET_ACTIVITY_EXCEPTION_MESSAGE_PATTERN = "SequenceFlow '%s' has no target activity";

  @Override
  public Collection<ProcessValidationWorkerMarker> validate(final Diagram diagram, final Map<String, List<EObject>> processNodes) {

    final Collection<ProcessValidationWorkerMarker> result = new ArrayList<ProcessValidationWorkerMarker>();

    final List<EObject> sequenceFlows = processNodes.get(SequenceFlowImpl.class.getCanonicalName());

    if (sequenceFlows != null && !sequenceFlows.isEmpty()) {
      for (final EObject object : sequenceFlows) {

        final SequenceFlow sequenceFlow = (SequenceFlow) object;
        if (sequenceFlow.getSourceRef() == null || sequenceFlow.getSourceRef().getId() == null || sequenceFlow.getSourceRef().getId().length() == 0) {
          result.add(new ProcessValidationWorkerMarker(IMarker.SEVERITY_ERROR, String.format(NO_SOURCE_ACTIVITY_EXCEPTION_MESSAGE_PATTERN,
                  sequenceFlow.getName()), sequenceFlow.getId(), ValidationCode.VAL_005));
        }
        if (sequenceFlow.getTargetRef() == null || sequenceFlow.getTargetRef().getId() == null || sequenceFlow.getTargetRef().getId().length() == 0) {
          result.add(new ProcessValidationWorkerMarker(IMarker.SEVERITY_ERROR, String.format(NO_TARGET_ACTIVITY_EXCEPTION_MESSAGE_PATTERN,
                  sequenceFlow.getName()), sequenceFlow.getId(), ValidationCode.VAL_006));
        }
      }
    }

    return result;
  }
}
