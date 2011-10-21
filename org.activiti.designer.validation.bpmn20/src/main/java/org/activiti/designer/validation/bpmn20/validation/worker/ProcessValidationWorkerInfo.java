package org.activiti.designer.validation.bpmn20.validation.worker;

/**
 * Container object for {@link ProcessValidationWorker}s and their work units.
 * 
 * @author Tiese Barrell
 * @since 5.6
 * @version 1
 */
public class ProcessValidationWorkerInfo {

  private final ProcessValidationWorker processValidationWorker;
  private final int work;

  public ProcessValidationWorkerInfo(ProcessValidationWorker processValidationWorker, int work) {
    super();
    this.processValidationWorker = processValidationWorker;
    this.work = work;
  }

  public ProcessValidationWorker getProcessValidationWorker() {
    return processValidationWorker;
  }

  public int getWork() {
    return work;
  }

}
