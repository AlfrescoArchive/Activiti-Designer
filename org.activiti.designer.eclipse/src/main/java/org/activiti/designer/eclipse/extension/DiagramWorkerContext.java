package org.activiti.designer.eclipse.extension;

import org.activiti.designer.util.editor.Bpmn2MemoryModel;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Context for classes that perform work on diagrams.
 * 
 * <p>
 * The {@link IProgressMonitor} obtained from {@link #getProgressMonitor()} can
 * be used to indicate progress made in the worker and will be reported to the
 * user as part of the overall progress of work on the diagram.
 * 
 * @author Tiese Barrell
 * 
 */
public interface DiagramWorkerContext {

  /**
   * Gets the {@link IProgressMonitor} to be used to report progress on the work
   * being performed.
   * 
   * @return the progress monitor
   */
  public IProgressMonitor getProgressMonitor();

  /**
   * Gets the internal BPMN memory model of the diagram being worked on.
   * 
   * @return the Bpmn2MemoryModel of the diagram
   */
  public Bpmn2MemoryModel getBpmnModel();

}
