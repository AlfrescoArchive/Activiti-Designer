package org.activiti.designer.validation.bpmn20.validation.worker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.designer.bpmn2.model.UserTask;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorker;
import org.activiti.designer.validation.bpmn20.validation.worker.ProcessValidationWorkerMarker;
import org.eclipse.core.resources.IMarker;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Validates process according to {@link ValidationCode#VAL_001}.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class UserTaskValidationWorker implements ProcessValidationWorker {

  private static final String POTENTIAL_OWNER_EXCEPTION_MESSAGE_PATTERN = "UserTask '%s' has no assignee, candidate users, candidate groups set";

  @Override
  public Collection<ProcessValidationWorkerMarker> validate(final Diagram diagram, final Map<String, List<Object>> processNodes) {

    final Collection<ProcessValidationWorkerMarker> result = new ArrayList<ProcessValidationWorkerMarker>();

    final List<Object> userTasks = processNodes.get(UserTask.class.getCanonicalName());

    if (userTasks != null && !userTasks.isEmpty()) {
      for (final Object object : userTasks) {

        final UserTask userTask = (UserTask) object;

        boolean potentialOwnerIsSet = false;
        if (userTask.getAssignee() != null && userTask.getAssignee().length() > 0) {
          potentialOwnerIsSet = true;
        }
        if (userTask.getCandidateUsers() != null && userTask.getCandidateUsers().size() > 0) {
          potentialOwnerIsSet = true;
        }
        if (userTask.getCandidateGroups() != null && userTask.getCandidateGroups().size() > 0) {
          potentialOwnerIsSet = true;
        }

        if (!potentialOwnerIsSet) {
          result.add(new ProcessValidationWorkerMarker(IMarker.SEVERITY_ERROR, String.format(POTENTIAL_OWNER_EXCEPTION_MESSAGE_PATTERN, userTask.getName()),
                  userTask.getId(), ValidationCode.VAL_001));
        }
      }
    }

    return result;
  }
}
