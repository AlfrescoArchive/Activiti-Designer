/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
