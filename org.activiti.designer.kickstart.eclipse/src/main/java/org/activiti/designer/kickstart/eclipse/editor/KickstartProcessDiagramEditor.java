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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.activiti.designer.kickstart.eclipse.common.KickstartPlugin;
import org.activiti.designer.kickstart.eclipse.util.FileService;
import org.activiti.designer.util.editor.KickstartProcessMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.alfresco.conversion.json.AlfrescoSimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class KickstartProcessDiagramEditor extends DiagramEditor {

  private static GraphicalViewer activeGraphicalViewer;

  private TransactionalEditingDomain transactionalEditingDomain;

  public KickstartProcessDiagramEditor() {
    super();
  }

  @Override
  public TransactionalEditingDomain getEditingDomain() {
    TransactionalEditingDomain ted = super.getEditingDomain();

    if (ted == null) {
      ted = transactionalEditingDomain;
    }

    return ted;
  }

  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    IEditorInput finalInput = null;

    try {
      if (input instanceof KickstartDiagramEditorInput) {
        finalInput = input;
      } else {
        finalInput = createNewDiagramEditorInput(input);
      }
    } catch (CoreException exception) {
      exception.printStackTrace();
    }

    super.init(site, finalInput);
  }

  private KickstartDiagramEditorInput createNewDiagramEditorInput(final IEditorInput input) throws CoreException {

    final IFile dataFile = FileService.getDataFileForInput(input);

    // now generate the temporary diagram file
    final IPath dataFilePath = dataFile.getFullPath();

    // get or create the corresponding temporary folder
    final IFolder tempFolder = FileService.getOrCreateTempFolder(dataFilePath);

    // finally get the diagram file that corresponds to the data file
    final IFile diagramFile = FileService.getTemporaryDiagramFile(dataFilePath, tempFolder);

    // Create new temporary diagram file
    KickstartProcessDiagramCreator creator = new KickstartProcessDiagramCreator();

    return creator.creatProcessDiagram(dataFile, diagramFile, this, null, false);
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    super.doSave(monitor);

    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) getEditorInput();

    try {
      final IFile dataFile = adei.getDataFile();
      final String diagramFileString = dataFile.getLocationURI().getPath();

      KickstartProcessMemoryModel model = ModelHandler.getKickstartProcessModel(EcoreUtil
          .getURI(getDiagramTypeProvider().getDiagram()));

      AlfrescoSimpleWorkflowJsonConverter converter = new AlfrescoSimpleWorkflowJsonConverter();
      File objectsFile = new File(diagramFileString);
      FileWriter writer = new FileWriter(objectsFile);
      converter.writeWorkflowDefinition(model.getWorkflowDefinition(), writer);
      writer.close();

      dataFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ((BasicCommandStack) getEditingDomain().getCommandStack()).saveIsDone();
    updateDirtyState();
  }

  @Override
  public boolean isDirty() {
    TransactionalEditingDomain editingDomain = getEditingDomain();
    // Check that the editor is not yet disposed
    if (editingDomain != null && editingDomain.getCommandStack() != null) {
      return ((BasicCommandStack) editingDomain.getCommandStack()).isSaveNeeded();
    }
    return false;
  }

  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);

    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) input;
    final IFile dataFile = adei.getDataFile();

    final KickstartProcessMemoryModel model = new KickstartProcessMemoryModel(getDiagramTypeProvider()
        .getFeatureProvider(), dataFile);
    ModelHandler.addModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()), model);

    String filePath = dataFile.getLocationURI().getPath();
    File kickstartProcessFile = new File(filePath);
    try {
      if (kickstartProcessFile.exists() == false) {
        model.setWorkflowDefinition(new WorkflowDefinition());
        kickstartProcessFile.createNewFile();
        dataFile.refreshLocal(IResource.DEPTH_INFINITE, null);
      } else {
        FileInputStream fileStream = new FileInputStream(kickstartProcessFile);
        AlfrescoSimpleWorkflowJsonConverter converter = new AlfrescoSimpleWorkflowJsonConverter();
        WorkflowDefinition definition = null;
        try {
          definition = converter.readWorkflowDefinition(fileStream);
        } catch (Exception e) {
          definition = new WorkflowDefinition();
          Status errorStatus = null;
          if(e.getCause() != null) {
            errorStatus = new Status(IStatus.ERROR, KickstartPlugin.PLUGIN_ID, e.getCause().getMessage());
          } else {
            errorStatus = new Status(IStatus.ERROR, KickstartPlugin.PLUGIN_ID, e.getMessage());
          }
          ErrorDialog.openError(getSite().getShell(), "Error", "An error occured while reading kickstart process file.", errorStatus);
        }
        model.setWorkflowDefinition(definition);

        BasicCommandStack basicCommandStack = (BasicCommandStack) getEditingDomain().getCommandStack();

        if (input instanceof DiagramEditorInput) {

          basicCommandStack.execute(new RecordingCommand(getEditingDomain()) {

            @Override
            protected void doExecute() {
              importDiagram(model);

              // Hide the grid
              getDiagramTypeProvider().getDiagram().setGridUnit(-1);
            }
          });
        }
        basicCommandStack.saveIsDone();
        basicCommandStack.flush();
      }

      model.setInitialized(true);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void importDiagram(final KickstartProcessMemoryModel model) {
    final Diagram diagram = getDiagramTypeProvider().getDiagram();
    diagram.setActive(true);
    getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

      @Override
      protected void doExecute() {
        IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

        // Provide -1 as x and y, to force layout adding the shape at the end
        AreaContext areaContext = new AreaContext();
        areaContext.setY(-1);
        areaContext.setX(-1);

        // Add steps in reverse order to have correct layout
        StepDefinition step = null;
        for (int i = model.getWorkflowDefinition().getSteps().size() - 1; i >= 0; i--) {
          step = model.getWorkflowDefinition().getSteps().get(i);
          AddContext addContext = new AddContext(areaContext, step);
          addContext.setTargetContainer(getDiagramTypeProvider().getDiagram());
          featureProvider.getAddFeature(addContext).add(addContext);
        }
      }
    });
  }

  public static GraphicalViewer getActiveGraphicalViewer() {
    return activeGraphicalViewer;
  }

  @Override
  public void dispose() {
    super.dispose();

    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) getEditorInput();

    ModelHandler.getKickstartProcessModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
    KickstartProcessDiagramCreator.dispose(adei.getDiagramFile());
  }
}
