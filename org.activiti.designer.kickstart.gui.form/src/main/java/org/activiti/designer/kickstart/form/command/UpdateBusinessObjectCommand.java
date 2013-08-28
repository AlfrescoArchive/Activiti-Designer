package org.activiti.designer.kickstart.form.command;

import org.eclipse.emf.edit.command.AbstractOverrideableCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Abstract command that runs a {@link KickstartModelUpdater} inside a EMF-command, to
 * take part in the current {@link TransactionalEditingDomain}.
 * 
 * @author Frederik Heremans
 */
public class UpdateBusinessObjectCommand extends AbstractOverrideableCommand {

  private KickstartModelUpdater<?> updater;

  public UpdateBusinessObjectCommand(EditingDomain domain, KickstartModelUpdater<?> updater) {
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
