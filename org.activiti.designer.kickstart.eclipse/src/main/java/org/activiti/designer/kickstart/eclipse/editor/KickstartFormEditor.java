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

import org.activiti.designer.kickstart.eclipse.Logger;
import org.activiti.designer.kickstart.eclipse.util.FileService;
import org.activiti.designer.util.editor.KickstartFormMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.workflow.simple.alfresco.conversion.json.AlfrescoSimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyGroup;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class KickstartFormEditor extends DiagramEditor {

  private static GraphicalViewer activeGraphicalViewer;
  private AlfrescoSimpleWorkflowJsonConverter jsonConverter;

  public KickstartFormEditor() {
    jsonConverter = new AlfrescoSimpleWorkflowJsonConverter();
  }

  public static GraphicalViewer getActiveGraphicalViewer() {
    return activeGraphicalViewer;
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    super.doSave(monitor);

    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) getEditorInput();
    
    try {
      final IFile dataFile = adei.getDataFile();
      final String diagramFileString = dataFile.getLocation().toOSString();

      File objectsFile = new File(diagramFileString);
      FileWriter writer = new FileWriter(objectsFile);
      
      // Convert the model into JSON and store it. Writer is closed by the converter
      KickstartFormMemoryModel model = ModelHandler.getKickstartFormMemoryModel(
          EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
      jsonConverter.writeFormDefinition(model.getFormDefinition(), writer);
      
      dataFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
    } catch (Exception e) {
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
  public void dispose() {
    super.dispose();

    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) getEditorInput();

    ModelHandler.removeModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
    KickstartProcessDiagramCreator.dispose(adei.getDiagramFile());
  }

  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    IEditorInput finalInput = null;

    try {
      if (input instanceof KickstartDiagramEditorInput) {
        // No need to wrap, already using the right type of input
        finalInput = input;
      } else {
        // Wrap in a KickstartDiagramEditorInput and use that instead
        finalInput = createNewDiagramEditorInput(input);
      }
    } catch (CoreException exception) {
      exception.printStackTrace();
    }

    super.init(site, finalInput);
  }

  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);

    final KickstartDiagramEditorInput adei = (KickstartDiagramEditorInput) input;
    final IFile dataFile = adei.getDataFile();

    final KickstartFormMemoryModel model = new KickstartFormMemoryModel(getDiagramTypeProvider().getFeatureProvider(),
        dataFile);
    ModelHandler.addModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()), model);

    String filePath = dataFile.getLocation().toOSString();
    File formDefinitionFile = new File(filePath);
    try {
      if (formDefinitionFile.exists() == false) {
        model.setFormDefinition(createEmptyFormDefinition());
        formDefinitionFile.createNewFile();
        dataFile.refreshLocal(IResource.DEPTH_INFINITE, null);
      } else {
        // Empty file, create default model
        FormDefinition definition = null;
        
        if(formDefinitionFile.length() == 0L) {
          definition = createEmptyFormDefinition();
          jsonConverter.writeFormDefinition(definition, new FileWriter(formDefinitionFile));
        } else {
          // Non-empty file, load contents
          FileInputStream fileStream = new FileInputStream(formDefinitionFile);
          try {
            definition = jsonConverter.readFormDefinition(fileStream);
          } catch(Exception e) {
            definition = createEmptyFormDefinition();
            
            // Show an error to the user, informing that the input file cannot be read
            // and an empty diagram is opened
            Logger.logError("Error while opening form diagram", e);
            getSite().getShell().getDisplay().asyncExec(
                new Runnable() {
                  public void run() {
                    MessageDialog.openError(getSite().getShell(), "Error while opening form", 
                        "An error occured while opening the form, make sure the content of the file is valid. See the error-log for additional details.");
                  }
                });
          }
        }
        
        model.setFormDefinition(definition);

        final FormDefinition definitionInModel = definition;
        BasicCommandStack basicCommandStack = (BasicCommandStack) getEditingDomain().getCommandStack();

        if (input instanceof DiagramEditorInput) {
          basicCommandStack.execute(new RecordingCommand(getEditingDomain()) {
            @Override
            protected void doExecute() {
              importDiagram(definitionInModel);
              
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
  
  protected FormDefinition createEmptyFormDefinition() {
    FormDefinition definition = new FormDefinition();
    FormPropertyGroup infoGroup = new FormPropertyGroup();
    infoGroup.setId(KickstartFormMemoryModel.INFO_GROUP_ID);
    infoGroup.setTitle("Info");
    
    definition.addFormPropertyGroup(infoGroup);
    return definition;
  }

  protected void importDiagram(FormDefinition formDefinition) {
    IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
    AddContext addContext = null;
    
    // Provide -1 as x and y, to force layout adding the shape at the end
    AreaContext areaContext = new AreaContext();
    areaContext.setY(-1);
    areaContext.setX(-1);
    
    FormPropertyGroup group = null;
    // Loop over the groups backwards and add y-location of 0 to retain the actual order
    for(int i=formDefinition.getFormGroups().size() - 1; i>=0; i--) {
      group = formDefinition.getFormGroups().get(i);
      addContext = new AddContext(areaContext, group);
      addContext.setTargetContainer(getDiagramTypeProvider().getDiagram());
      PictogramElement addedGroupPE = featureProvider.getAddFeature(addContext).add(addContext);
      
      // Add properties to the group
      for(FormPropertyDefinition defInGroup : group.getFormPropertyDefinitions()) {
        addContext = new AddContext(areaContext, defInGroup);
        addContext.setTargetContainer((ContainerShape) addedGroupPE);
        featureProvider.getAddFeature(addContext).execute(addContext);
      }
    }
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
    return new KickstartFormDiagramCreator().createFormDiagram(dataFile, diagramFile, this);
  }

}
