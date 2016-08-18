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
package org.activiti.designer.features.contextmenu;

import java.util.Set;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.workspace.ActivitiWorkspaceUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


public class OpenCalledElementForCallActivity extends AbstractCustomFeature {

  public OpenCalledElementForCallActivity(final IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public String getDescription() {
    return "Opens a specified call element for a call activity.";
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    final CallActivity ca = getCallActivity(context);

    if (ca == null) {
      return super.canExecute(context);
    } else {
      return true;
    }
  }

  @Override
  public String getName() {
    return "Open Called Element";
  }

  @Override
  public void execute(ICustomContext context) {
    final CallActivity ca = getCallActivity(context);
    final String calledElement = ca.getCalledElement();

    if (calledElement != null && StringUtils.isNotBlank(calledElement)) {
      final Set<IFile> dataFiles = ActivitiWorkspaceUtil.getDiagramDataFilesByProcessId(calledElement);

      if (dataFiles.size() == 1) {
        // we only handle this if it is not ambiguous
        final IFile dataFile = dataFiles.iterator().next();

        openDiagramForBpmnFile(dataFile);
      }
    }
  }

  /**
   * Opens the given diagram specified by the given data file in a new editor. In case an error
   * occurs while doing so, opens an error dialog.
   *
   * @param dataFile the data file to use for the new editor to open
   *
   * TODO: this is a copy from PropertyCallActivitySection. Figure out how to make sure we do not double this
   */
  private void openDiagramForBpmnFile(IFile dataFile) {

    if (dataFile.exists())
    {
      final IWorkbenchPage activePage
        = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

      try {
        IDE.openEditor(activePage, dataFile, ActivitiConstants.DIAGRAM_EDITOR_ID, true);
      } catch (PartInitException exception) {
        final IStatus status = new Status(IStatus.ERROR, ActivitiPlugin.getID()
                                        , "Error while opening new editor.", exception);

        ErrorDialog.openError(Display.getCurrent().getActiveShell()
                            , "Error Opening Activiti Diagram", null, status);
      }
    }
  }

  private CallActivity getCallActivity(final ICustomContext context) {
    final PictogramElement[] pes = context.getPictogramElements();

    if (pes != null) {
      for (final PictogramElement pe : pes) {
        final Object bo = getBusinessObjectForPictogramElement(pe);
        if (bo instanceof CallActivity) {
          return (CallActivity) bo;
        }
      }
    }

    return null;
  }
}
