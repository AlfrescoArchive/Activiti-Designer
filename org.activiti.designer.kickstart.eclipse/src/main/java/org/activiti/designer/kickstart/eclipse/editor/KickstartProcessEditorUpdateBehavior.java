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
package org.activiti.designer.kickstart.eclipse.editor;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

/**
 * Subclasses the default update behavior of Graphiti to all external creation of the transactional
 * editing domain. This allows us to not create one externally via internal API but rely on the
 * standard way of doing it ... well mostly.
 *
 * @author Heiko Kopp
 */
public class KickstartProcessEditorUpdateBehavior extends DefaultUpdateBehavior {

  public KickstartProcessEditorUpdateBehavior(DiagramBehavior diagramBehavior) {
    super(diagramBehavior);
  }

  @Override
  public TransactionalEditingDomain getEditingDomain() {
    if (super.getEditingDomain() == null) {
      createEditingDomain(null);
    }

    return super.getEditingDomain();
  }

  @Override
  protected boolean isAdapterActive() {
    return false;
  }

}
