/**
 * 
 */
package org.activiti.designer.eclipse.extension.export;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.extension.AbstractDiagramWorker;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * Base class for {@link ExportMarshaller} implementations.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 3
 * 
 */
public abstract class AbstractExportMarshaller extends AbstractDiagramWorker implements ExportMarshaller {

  private static final int WORK_INVOKE_VALIDATORS_VALIDATOR = 10;

  /**
   * Invokes validators marked by the provided validatorIds. If no validator is
   * registered by one of the validatorIds, that validator is skipped. Make sure
   * to provide a fresh {@link SubProgressMonitor} as a monitor to properly
   * incorporate progress reporting into that of the originating
   * ExportMarshaller.
   * 
   * @param validatorIds
   *          the list of ids of the validators to invoke
   * @return true if *all* of the validators completed successfully or false
   *         otherwise
   */
  protected boolean invokeValidators(final List<String> validatorIds, final Diagram diagram, final IProgressMonitor monitor) {

    final int totalWork = WORK_INVOKE_VALIDATORS_VALIDATOR * validatorIds.size();

    final IProgressMonitor activeMonitor = monitor == null ? new NullProgressMonitor() : monitor;

    activeMonitor.beginTask("Invoking validators", totalWork);

    boolean overallResult = true;

    try {

      if (validatorIds.size() > 0) {

        for (final String validatorId : validatorIds) {

          // get validator, else skip
          final ProcessValidator processValidator = ExtensionPointUtil.getProcessValidator(validatorId);

          if (processValidator != null) {

            monitor.subTask("Invoking " + processValidator.getValidatorName());

            if (!(processValidator.validateDiagram(diagram, new SubProgressMonitor(activeMonitor, WORK_INVOKE_VALIDATORS_VALIDATOR)))) {
              // don't break if one result is false: keep validating to get
              // all of the problems
              overallResult = false;
            }
          }
        }
      }

    } finally {
      monitor.done();
    }

    return overallResult;
  }
  /**
   * Invokes validator marked by the provided validatorId. If no validator is
   * registered by the validatorId, that validator is skipped. Make sure to
   * provide a fresh {@link SubProgressMonitor} as a monitor to properly
   * incorporate progress reporting into that of the originating
   * ExportMarshaller.
   * 
   * @param validatorId
   *          the id of the validator to invoke
   * @return true if the validator completed successfully or false otherwise
   */
  protected boolean invokeValidator(final String validatorId, final Diagram diagram, final IProgressMonitor monitor) {
    final List<String> validatorIds = new ArrayList<String>();
    validatorIds.add(validatorId);
    return invokeValidators(validatorIds, diagram, monitor);
  }

}
