package org.activiti.designer.validation.bpmn20.validation.worker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.impl.ScriptTaskImpl;
import org.eclipse.core.resources.IMarker;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Validates process according to {@link ValidationCode#VAL_002} and
 * {@link ValidationCode#VAL_003}.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class ScriptTaskValidationWorker implements ProcessValidationWorker {

  private static final String NO_FORMAT_EXCEPTION_MESSAGE_PATTERN = "ScriptTask '%s' has no format specified";
  private static final String NO_SCRIPT_EXCEPTION_MESSAGE_PATTERN = "ScriptTask '%s' has no format specified";

  @Override
  public Collection<ProcessValidationWorkerMarker> validate(final Diagram diagram, final Map<String, List<EObject>> processNodes) {

    final Collection<ProcessValidationWorkerMarker> result = new ArrayList<ProcessValidationWorkerMarker>();

    final List<EObject> scriptTasks = processNodes.get(ScriptTaskImpl.class.getCanonicalName());

    if (scriptTasks != null && !scriptTasks.isEmpty()) {
      for (final EObject object : scriptTasks) {

        final ScriptTask scriptTask = (ScriptTask) object;
        if (scriptTask.getScriptFormat() == null || scriptTask.getScriptFormat().length() == 0) {
          result.add(new ProcessValidationWorkerMarker(IMarker.SEVERITY_ERROR, String.format(NO_FORMAT_EXCEPTION_MESSAGE_PATTERN, scriptTask.getName()),
                  scriptTask.getId(), ValidationCode.VAL_002));
        }
        if (scriptTask.getScript() == null || scriptTask.getScript().length() == 0) {
          result.add(new ProcessValidationWorkerMarker(IMarker.SEVERITY_ERROR, String.format(NO_SCRIPT_EXCEPTION_MESSAGE_PATTERN, scriptTask.getName()),
                  scriptTask.getId(), ValidationCode.VAL_003));
        }
      }
    }

    return result;
  }
}
