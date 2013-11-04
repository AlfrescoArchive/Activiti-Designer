package org.activiti.designer.command;

import org.eclipse.emf.edit.command.AbstractOverrideableCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Abstract command that runs a {@link BpmnProcessModelUpdater} inside a EMF-command, to
 * take part in the current {@link TransactionalEditingDomain}.
 * 
 * @author Tijs Rademakers
 */
public class UpdateBusinessObjectCommand extends AbstractOverrideableCommand {

  private BpmnProcessModelUpdater updater;

  public UpdateBusinessObjectCommand(EditingDomain domain, BpmnProcessModelUpdater updater) {
    super(domain);
    this.updater = updater;
  } 
  
  @Override
  public final void doExecute() {
    updater.doUpdate();
  }
  
  @Override
  public final void doUndo() {
    updater.doUndo();
  }
  
  @Override
  public final void doRedo() {
    updater.doUpdate();
  }
  
  @Override
  public boolean doCanExecute() {
    return true;
  }
  
  @Override
  public boolean doCanUndo() {
    return true;
  }
}
