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
package org.activiti.designer.eclipse.editor;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Artifact;
import org.activiti.bpmn.model.Association;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.DataObject;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FieldExtension;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowElementsContainer;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.MessageFlow;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.ServiceTask;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.ui.ExportMarshallerRunnable;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.eclipse.util.FileService;
import org.activiti.designer.integration.annotation.Property;
import org.activiti.designer.integration.servicetask.CustomServiceTask;
import org.activiti.designer.integration.usertask.CustomUserTask;
import org.activiti.designer.util.bpmn.BpmnExtensions;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.activiti.designer.util.extension.ExtensionUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class ActivitiDiagramEditor extends DiagramEditor {

  private static GraphicalViewer activeGraphicalViewer;

  private TransactionalEditingDomain transactionalEditingDomain;

  public ActivitiDiagramEditor() {
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
      if (input instanceof ActivitiDiagramEditorInput) {
        finalInput = input;
      } else {
        finalInput = createNewDiagramEditorInput(input);
      }
    } catch (CoreException exception) {
      exception.printStackTrace();
    }

    super.init(site, finalInput);
  }

  private ActivitiDiagramEditorInput createNewDiagramEditorInput(final IEditorInput input) throws CoreException {

    final IFile dataFile = FileService.getDataFileForInput(input);

    // now generate the temporary diagram file
    final IPath dataFilePath = dataFile.getFullPath();

    // get or create the corresponding temporary folder
    final IFolder tempFolder = FileService.getOrCreateTempFolder(dataFilePath);

    // finally get the diagram file that corresponds to the data file
    final IFile diagramFile = FileService.getTemporaryDiagramFile(dataFilePath, tempFolder);

    // Create new temporary diagram file
    Bpmn2DiagramCreator creator = new Bpmn2DiagramCreator();

    return creator.createBpmnDiagram(dataFile, diagramFile, this, null, false);
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    super.doSave(monitor);

    final ActivitiDiagramEditorInput adei = (ActivitiDiagramEditorInput) getEditorInput();

    try {
      final IFile dataFile = adei.getDataFile();
      final String diagramFileString = dataFile.getLocationURI().getPath();
      BpmnMemoryModel model = ModelHandler.getModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));

      // Save the bpmn diagram file
      doSaveToBpmn(model, diagramFileString);

      // Save an image of the diagram
      doSaveImage(diagramFileString, model);

      // Refresh the resources in the workspace before invoking export
      // marshallers, as they may need access to resources
      dataFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

      // Invoke export marshallers to produce additional output
      doInvokeExportMarshallers(model);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ((BasicCommandStack) getEditingDomain().getCommandStack()).saveIsDone();
    updateDirtyState();
  }

  protected void doSaveToBpmn(final BpmnMemoryModel model, final String diagramFileString) throws Exception {

    // add sequence flow bend-points to the model
    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
    new GraphitiToBpmnDI(model, featureProvider).processGraphitiElements();

    BpmnXMLConverter converter = new BpmnXMLConverter();
    byte[] xmlBytes = converter.convertToXML(model.getBpmnModel());

    File objectsFile = new File(diagramFileString);
    try {
      FileOutputStream fos = new FileOutputStream(objectsFile);
      fos.write(xmlBytes);
      fos.close();
    } catch (Exception e) {
      MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
      messageBox.setText("Warning");
      messageBox.setMessage("Error while saving the model " + e.getLocalizedMessage());
      messageBox.open();
    }

  }

  private void doSaveImage(final String diagramFileString, BpmnMemoryModel model) {
    boolean saveImage = PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE, ActivitiPlugin.getDefault());
    if (saveImage) {
      List<String> languages = PreferencesUtil.getStringArray(Preferences.ACTIVITI_LANGUAGES, ActivitiPlugin.getDefault());
      if (languages != null && languages.size() > 0) {
        
        for (String language : languages) {
          for (Process process : model.getBpmnModel().getProcesses()) {
            fillContainerWithLanguage(process, language);
          }
          
          ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
          InputStream imageStream = processDiagramGenerator.generatePngDiagram(model.getBpmnModel());
          
          if (imageStream != null) {
            String imageFileName = null;
            if (diagramFileString.endsWith(".bpmn20.xml")) {
              imageFileName = diagramFileString.substring(0, diagramFileString.length() - 11) + 
                  "_" + language + ".png";
            } else {
              imageFileName = diagramFileString.substring(0, diagramFileString.lastIndexOf(".")) + 
                  "_" + language + ".png";
            }
            File imageFile = new File(imageFileName);
            FileOutputStream outStream = null;
            ByteArrayOutputStream baos = null;
            try {
              outStream = new FileOutputStream(imageFile);
              baos = new ByteArrayOutputStream();
              IOUtils.copy(imageStream, baos);
              baos.writeTo(outStream);
              
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              if (outStream != null) {
                IOUtils.closeQuietly(outStream);
              }
              if (baos != null) {
                IOUtils.closeQuietly(baos);
              }
            }
          }
        }
 
      } else {
        marshallImage(model, diagramFileString);
      }
    }
  }
  
  protected void fillContainerWithLanguage(FlowElementsContainer container, String language) {
    for (FlowElement flowElement : container.getFlowElements()) {
      
      List<ExtensionElement> languageElements = flowElement.getExtensionElements().get(BpmnExtensions.LANGUAGE_EXTENSION);
      
      if (languageElements != null && languageElements.size() > 0) {
        for (ExtensionElement extensionElement : languageElements) {
          List<ExtensionAttribute> languageAttributes = extensionElement.getAttributes().get("language");
          if (languageAttributes != null && languageAttributes.size() == 1) {
            String languageValue = languageAttributes.get(0).getValue();
            if (language.equals(languageValue)) {
              flowElement.setName(extensionElement.getElementText());
            }
          }
        }
      }
      
      if (flowElement instanceof SubProcess) {
        fillContainerWithLanguage((SubProcess) flowElement, language);
      }
    }
  }

  private void marshallImage(BpmnMemoryModel model, String modelFileName) {
    try {
      final GraphicalViewer graphicalViewer = (GraphicalViewer) ((DiagramEditor) model.getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer())
              .getAdapter(GraphicalViewer.class);

      if (graphicalViewer == null || graphicalViewer.getEditPartRegistry() == null) {
        return;
      }
      final ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
      final IFigure rootFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);
      final IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      final Rectangle rootFigureBounds = rootFigure.getBounds();

      final boolean toggleRequired = gridFigure.isShowing();

      final Display display = Display.getDefault();

      final Image img = new Image(display, rootFigureBounds.width, rootFigureBounds.height);
      final GC imageGC = new GC(img);

      // Add overlay
      addOverlay(imageGC, modelFileName, model);

      final SWTGraphics grap = new SWTGraphics(imageGC);

      // Access UI thread from runnable to print the canvas to the image
      display.syncExec(new Runnable() {

        @Override
        public void run() {
          if (toggleRequired) {
            // Disable any grids temporarily
            gridFigure.setVisible(false);
          }
          // Deselect any selections
          graphicalViewer.deselectAll();
          rootFigure.paint(grap);
        }
      });

      ImageLoader imgLoader = new ImageLoader();
      imgLoader.data = new ImageData[] { img.getImageData() };

      ByteArrayOutputStream baos = new ByteArrayOutputStream(imgLoader.data.length);

      imgLoader.save(baos, SWT.IMAGE_PNG);

      imageGC.dispose();
      img.dispose();

      // Access UI thread from runnable
      display.syncExec(new Runnable() {

        @Override
        public void run() {
          if (toggleRequired) {
            // Re-enable any grids
            gridFigure.setVisible(true);
          }
        }
      });

      String imageFileName = null;
      if (modelFileName.endsWith(".bpmn20.xml")) {
        imageFileName = modelFileName.substring(0, modelFileName.length() - 11) + ".png";
      } else {
        imageFileName = modelFileName.substring(0, modelFileName.lastIndexOf(".")) + ".png";
      }
      File imageFile = new File(imageFileName);
      FileOutputStream outStream = new FileOutputStream(imageFile);
      baos.writeTo(outStream);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void addOverlay(final GC imageGC, String modelFileName, BpmnMemoryModel model) {
    if (PreferencesUtil.getBooleanPreference(Preferences.SAVE_IMAGE_ADD_OVERLAY, ActivitiPlugin.getDefault())) {
      final ImageOverlayCreator creator = new ImageOverlayCreator(imageGC);
      creator.addOverlay(modelFileName, model);
    }
  }

  private void doInvokeExportMarshallers(final BpmnMemoryModel model) throws InvocationTargetException, InterruptedException {
    final Collection<ExportMarshaller> marshallers = ExtensionPointUtil.getExportMarshallers();
    final ExportMarshallerRunnable runnable = new ExportMarshallerRunnable(model, marshallers);
    final IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
    progressService.busyCursorWhile(runnable);
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

    final ActivitiDiagramEditorInput adei = (ActivitiDiagramEditorInput) input;
    final IFile dataFile = adei.getDataFile();

    final BpmnMemoryModel model = new BpmnMemoryModel(getDiagramTypeProvider().getFeatureProvider(), dataFile);
    ModelHandler.addModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()), model);

    String filePath = dataFile.getLocationURI().getPath();
    File bpmnFile = new File(filePath);
    try {
      if (bpmnFile.exists() == false) {
        model.setBpmnModel(new BpmnModel());
        model.addMainProcess();
        bpmnFile.createNewFile();
        dataFile.refreshLocal(IResource.DEPTH_INFINITE, null);
      } else {
        FileInputStream fileStream = new FileInputStream(bpmnFile);
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(fileStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnXMLConverter bpmnConverter = new BpmnXMLConverter();
        bpmnConverter.setUserTaskFormTypes(PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_USERTASK, ActivitiPlugin.getDefault()));
        bpmnConverter.setStartEventFormTypes(PreferencesUtil.getStringArray(Preferences.ALFRESCO_FORMTYPES_STARTEVENT, ActivitiPlugin.getDefault()));
        BpmnModel bpmnModel = null;
        try {
          bpmnModel = bpmnConverter.convertToBpmnModel(xtr);
        } catch (Exception e) {
          bpmnModel = new BpmnModel();
        }
        model.setBpmnModel(bpmnModel);

        if (bpmnModel.getLocationMap().size() == 0) {
          BpmnAutoLayout layout = new BpmnAutoLayout(bpmnModel);
          layout.execute();
        }

        BasicCommandStack basicCommandStack = (BasicCommandStack) getEditingDomain().getCommandStack();

        if (input instanceof DiagramEditorInput) {

          basicCommandStack.execute(new RecordingCommand(getEditingDomain()) {

            @Override
            protected void doExecute() {
              importDiagram(model);
            }
          });
        }
        basicCommandStack.saveIsDone();
        basicCommandStack.flush();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void importDiagram(final BpmnMemoryModel model) {
    final Diagram diagram = getDiagramTypeProvider().getDiagram();
    diagram.setActive(true);

    getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {

      @Override
      protected void doExecute() {
        if (model.getBpmnModel().getPools().size() > 0) {
          for (Pool pool : model.getBpmnModel().getPools()) {
            GraphicInfo graphicInfo = model.getBpmnModel().getGraphicInfo(pool.getId());

            // if no graphic info is present we can try to calculate it from the
            // lane DI info
            if (graphicInfo == null && StringUtils.isNotEmpty(pool.getProcessRef())) {
              Process process = model.getBpmnModel().getProcess(pool.getId());

              if (process != null && process.getLanes().size() > 0) {
                Double minX = null, minY = null, width = null, height = null;
                for (Lane lane : process.getLanes()) {
                  GraphicInfo laneInfo = model.getBpmnModel().getGraphicInfo(lane.getId());
                  if (laneInfo != null) {
                    if (minX == null || laneInfo.getX() < minX) {
                      minX = laneInfo.getX();
                    }
                    if (minY == null || laneInfo.getY() < minY) {
                      minY = laneInfo.getY();
                    }

                    if (width == null || laneInfo.getWidth() > width) {
                      width = laneInfo.getWidth();
                    }
                    if (height == null) {
                      height = laneInfo.getHeight();
                    } else {
                      height += laneInfo.getHeight();
                    }
                  }
                }

                if (width != null && width > 0) {
                  graphicInfo = new GraphicInfo();
                  graphicInfo.setX(minX);
                  graphicInfo.setY(minY);
                  graphicInfo.setWidth(width);
                  graphicInfo.setHeight(height);
                  model.getBpmnModel().addGraphicInfo(pool.getId(), graphicInfo);
                }
              }
            }

            if (graphicInfo != null) {
              PictogramElement poolElement = addContainerElement(pool, model, diagram);
              if (poolElement == null) {
                continue;
              }

              Process process = model.getBpmnModel().getProcess(pool.getId());
              if (process != null) {
                for (Lane lane : process.getLanes()) {
                  addContainerElement(lane, model, (ContainerShape) poolElement);
                }
              }
            }
          }
        }
        
        for (Process process : model.getBpmnModel().getProcesses()) {
          drawFlowElements(process.getFlowElements(), model.getBpmnModel().getLocationMap(), diagram, process);
          drawArtifacts(process, model.getBpmnModel().getLocationMap(), diagram, process);
        }
        drawAllFlows(model);
        drawMessageFlows(model.getBpmnModel().getMessageFlows().values(), model);
      }
    });
  }

  private PictogramElement addContainerElement(BaseElement element, BpmnMemoryModel model, ContainerShape parent) {
    GraphicInfo graphicInfo = model.getBpmnModel().getGraphicInfo(element.getId());
    if (graphicInfo == null) {
      return null;
    }

    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

    AddContext context = new AddContext(new AreaContext(), element);
    IAddFeature addFeature = featureProvider.getAddFeature(context);
    context.setNewObject(element);
    context.setSize((int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());
    context.setTargetContainer(parent);

    int x = (int) graphicInfo.getX();
    int y = (int) graphicInfo.getY();

    if (parent instanceof Diagram == false) {
      x = x - parent.getGraphicsAlgorithm().getX();
      y = y - parent.getGraphicsAlgorithm().getY();
    }

    context.setLocation(x, y);

    PictogramElement pictElement = null;
    if (addFeature.canAdd(context)) {
      pictElement = addFeature.add(context);
      featureProvider.link(pictElement, new Object[] { element });
    }

    return pictElement;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void drawFlowElements(Collection<FlowElement> elementList, Map<String, GraphicInfo> locationMap, ContainerShape parentShape, Process process) {

    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

    List<FlowElement> noDIList = new ArrayList<FlowElement>();
    for (FlowElement flowElement : elementList) {

      if (flowElement instanceof SequenceFlow) {
        continue;
      }

      AddContext context = new AddContext(new AreaContext(), flowElement);
      IAddFeature addFeature = featureProvider.getAddFeature(context);

      if (addFeature == null) {
        System.out.println("Element not supported: " + flowElement);
        return;
      }

      GraphicInfo graphicInfo = locationMap.get(flowElement.getId());
      if (graphicInfo == null) {

        noDIList.add(flowElement);

      } else {

        context.setNewObject(flowElement);
        context.setSize((int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());
        ContainerShape parentContainer = null;
        if (parentShape instanceof Diagram) {
          parentContainer = getParentContainer(flowElement.getId(), process, (Diagram) parentShape);
        } else {
          parentContainer = parentShape;
        }

        context.setTargetContainer(parentContainer);
        if (parentContainer instanceof Diagram == false) {
          Point location = getLocation(parentContainer);
          context.setLocation((int) graphicInfo.getX() - location.x, (int) graphicInfo.getY() - location.y);
        } else {
          context.setLocation((int) graphicInfo.getX(), (int) graphicInfo.getY());
        }

        if (flowElement instanceof ServiceTask) {

          ServiceTask serviceTask = (ServiceTask) flowElement;

          if (serviceTask.isExtended()) {

            CustomServiceTask targetTask = findCustomServiceTask(serviceTask);

            if (targetTask != null) {

              for (FieldExtension field : serviceTask.getFieldExtensions()) {
                CustomProperty customFieldProperty = new CustomProperty();
                customFieldProperty.setName(field.getFieldName());
                if (StringUtils.isNotEmpty(field.getExpression())) {
                  customFieldProperty.setSimpleValue(field.getExpression());
                } else {
                  customFieldProperty.setSimpleValue(field.getStringValue());
                }
                serviceTask.getCustomProperties().add(customFieldProperty);
              }

              serviceTask.getFieldExtensions().clear();
            }
          }
          
        } else if (flowElement instanceof UserTask) {

          UserTask userTask = (UserTask) flowElement;

          if (userTask.isExtended()) {

            CustomUserTask targetTask = findCustomUserTask(userTask);

            if (targetTask != null) {
              
              final List<Class<CustomUserTask>> classHierarchy = new ArrayList<Class<CustomUserTask>>();
              final List<String> fieldInfoObjects = new ArrayList<String>();

              Class clazz = targetTask.getClass();
              classHierarchy.add(clazz);

              boolean hierarchyOpen = true;
              while (hierarchyOpen) {
                clazz = clazz.getSuperclass();
                if (CustomUserTask.class.isAssignableFrom(clazz)) {
                  classHierarchy.add(clazz);
                } else {
                  hierarchyOpen = false;
                }
              }
              
              for (final Class<CustomUserTask> currentClass : classHierarchy) {
                for (final Field field : currentClass.getDeclaredFields()) {
                  if (field.isAnnotationPresent(Property.class)) {
                    fieldInfoObjects.add(field.getName());
                  }
                }
              }
              
              for (String fieldName : userTask.getExtensionElements().keySet()) {
                if (fieldInfoObjects.contains(fieldName)) {
                  CustomProperty customFieldProperty = new CustomProperty();
                  customFieldProperty.setName(fieldName);
                  customFieldProperty.setSimpleValue(userTask.getExtensionElements().get(fieldName).get(0).getElementText());
                  userTask.getCustomProperties().add(customFieldProperty);
                }
              }
              
              for (String fieldName : fieldInfoObjects) {
                userTask.getExtensionElements().remove(fieldName);
              }
            }
          }
        }

        if (flowElement instanceof BoundaryEvent) {
          BoundaryEvent boundaryEvent = (BoundaryEvent) flowElement;
          if (boundaryEvent.getAttachedToRef() != null) {
            ContainerShape container = (ContainerShape) featureProvider.getPictogramElementForBusinessObject(boundaryEvent.getAttachedToRef());

            if (container != null) {
              AddContext boundaryContext = new AddContext(new AreaContext(), boundaryEvent);
              boundaryContext.setTargetContainer(container);
              Point location = getLocation(container);
              boundaryContext.setLocation((int) graphicInfo.getX() - location.x, (int) graphicInfo.getY() - location.y);

              if (addFeature.canAdd(boundaryContext)) {
                addFeature.add(boundaryContext);
              }
            }
          }
        } else if (addFeature.canAdd(context)) {
          PictogramElement newContainer = addFeature.add(context);
          featureProvider.link(newContainer, new Object[] { flowElement });

          if (flowElement instanceof SubProcess) {
            drawFlowElements(((SubProcess) flowElement).getFlowElements(), locationMap, (ContainerShape) newContainer, process);
          }
        }
      }
    }

    for (FlowElement flowElement : noDIList) {
      if (flowElement instanceof BoundaryEvent) {
        ((BoundaryEvent) flowElement).getAttachedToRef().getBoundaryEvents().remove(flowElement);
      } else {
    	  // do not remove Data Objects
    	  if (flowElement instanceof DataObject == false) {
    		  elementList.remove(flowElement);
    	  }
      }
    }
  }

  protected CustomServiceTask findCustomServiceTask(ServiceTask serviceTask) {
    CustomServiceTask result = null;
    if (serviceTask.isExtended()) {

      final List<CustomServiceTask> customServiceTasks = ExtensionUtil.getCustomServiceTasks(
          ActivitiUiUtil.getProjectFromDiagram(getDiagramTypeProvider().getDiagram()));

      for (final CustomServiceTask customServiceTask : customServiceTasks) {
        if (serviceTask.getExtensionId().equals(customServiceTask.getId())) {
          result = customServiceTask;
          break;
        }
      }
    }
    return result;
  }
  
  protected CustomUserTask findCustomUserTask(UserTask userTask) {
    CustomUserTask result = null;
    if (userTask.isExtended()) {

      final List<CustomUserTask> customUserTasks = ExtensionUtil.getCustomUserTasks(
          ActivitiUiUtil.getProjectFromDiagram(getDiagramTypeProvider().getDiagram()));

      for (final CustomUserTask customUserTask : customUserTasks) {
        if (userTask.getExtensionId().equals(customUserTask.getId())) {
          result = customUserTask;
          break;
        }
      }
    }
    return result;
  }

  protected ContainerShape getParentContainer(String flowElementId, Process process, Diagram diagram) {
    Lane foundLane = null;
    for (Lane lane : process.getLanes()) {
      if (lane.getFlowReferences().contains(flowElementId)) {
        foundLane = lane;
        break;
      }
    }
    
    if (foundLane != null) {
      final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
      return (ContainerShape) featureProvider.getPictogramElementForBusinessObject(foundLane);
    } else {
      return diagram;
    }
  }

  protected Point getLocation(ContainerShape containerShape) {
    if (containerShape instanceof Diagram == true) {
      return new Point(containerShape.getGraphicsAlgorithm().getX(), containerShape.getGraphicsAlgorithm().getY());
    }
    
    Point location = getLocation(containerShape.getContainer());
    return new Point(location.x + containerShape.getGraphicsAlgorithm().getX(), location.y + containerShape.getGraphicsAlgorithm().getY());
  }
  
  protected void drawMessageFlows(final Collection<MessageFlow> messageFlows, final BpmnMemoryModel model) {

    for (final MessageFlow messageFlow : messageFlows) {
    
      Anchor sourceAnchor = null;
      Anchor targetAnchor = null;
      ContainerShape sourceShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(
              model.getFlowElement(messageFlow.getSourceRef()));
      
      if (sourceShape == null) {
        continue;
      }

      EList<Anchor> anchorList = sourceShape.getAnchors();
      for (Anchor anchor : anchorList) {
        if (anchor instanceof ChopboxAnchor) {
          sourceAnchor = anchor;
          break;
        }
      }

      ContainerShape targetShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(
              model.getFlowElement(messageFlow.getTargetRef()));
      
      if (targetShape == null) {
        continue;
      }

      anchorList = targetShape.getAnchors();
      for (Anchor anchor : anchorList) {
        if (anchor instanceof ChopboxAnchor) {
          targetAnchor = anchor;
          break;
        }
      }

      AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);

      List<GraphicInfo> bendpointList = new ArrayList<GraphicInfo>();
      if (model.getBpmnModel().getFlowLocationMap().containsKey(messageFlow.getId())) {
        List<GraphicInfo> pointList = model.getBpmnModel().getFlowLocationGraphicInfo(messageFlow.getId());
        if (pointList.size() > 2) {
          for (int i = 1; i < pointList.size() - 1; i++) {
            bendpointList.add(pointList.get(i));
          }
        }
      }
      addContext.putProperty("org.activiti.designer.bendpoints", bendpointList);
      addContext.putProperty("org.activiti.designer.connectionlabel", model.getBpmnModel().getLabelGraphicInfo(messageFlow.getId()));

      addContext.setNewObject(messageFlow);
      getDiagramTypeProvider().getFeatureProvider().addIfPossible(addContext);
    }
  }

  protected void drawArtifacts(final FlowElementsContainer container, final Map<String, GraphicInfo> locationMap, 
      final ContainerShape parent, final Process process) {

    final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

    final List<Artifact> artifactsWithoutDI = new ArrayList<Artifact>();
    for (final Artifact artifact : container.getArtifacts()) {

      if (artifact instanceof Association) {
        continue;
      }

      final AddContext context = new AddContext(new AreaContext(), artifact);
      final IAddFeature addFeature = featureProvider.getAddFeature(context);

      if (addFeature == null) {
        System.out.println("Element not supported: " + artifact);
        return;
      }

      final GraphicInfo gi = locationMap.get(artifact.getId());
      if (gi == null) {
        artifactsWithoutDI.add(artifact);
      } else {
        context.setNewObject(artifact);
        context.setSize((int) gi.getWidth(), (int) gi.getHeight());

        ContainerShape parentContainer = null;
        if (parent instanceof Diagram) {
          FlowElement connectingElement = null;
          for (final Artifact associationArtifact : container.getArtifacts()) {
            if (associationArtifact instanceof Association) {
              Association association = (Association) associationArtifact;
              
              if (association.getSourceRef().equals(artifact.getId())) {
                connectingElement = container.getFlowElement(association.getTargetRef());
                
              } else if (association.getTargetRef().equals(artifact.getId())) {
                connectingElement = container.getFlowElement(association.getSourceRef());
              }
            }
          }
          
          if (connectingElement != null) {
            parentContainer = getParentContainer(connectingElement.getId(), process, (Diagram) parent);
          } else {
            parentContainer = parent;
          }
        } else {
          parentContainer = parent;
        }

        context.setTargetContainer(parentContainer);
        if (parentContainer instanceof Diagram) {
          context.setLocation((int) gi.getX(), (int) gi.getY());
        } else {
          final Point location = getLocation(parentContainer);

          context.setLocation((int) gi.getX() - location.x, (int) gi.getY() - location.y);
        }

        if (addFeature.canAdd(context)) {
          final PictogramElement newContainer = addFeature.add(context);
          featureProvider.link(newContainer, new Object[] { artifact });
        }
      }
    }

    for (final Artifact artifact : artifactsWithoutDI) {
      container.getArtifacts().remove(artifact);
    }
    
    for (FlowElement flowElement : container.getFlowElements()) {
      if (flowElement instanceof SubProcess) {
        ContainerShape subProcessShape = (ContainerShape) featureProvider.getPictogramElementForBusinessObject(flowElement);
        drawArtifacts((SubProcess) flowElement, locationMap, subProcessShape, process);
      }
    }
  }

  protected void drawAllFlows(BpmnMemoryModel model) {
    BpmnModel bpmnModel = model.getBpmnModel();

    for (Process process : bpmnModel.getProcesses()) {
      drawSequenceFlowsInList(process.getFlowElements(), model);
      drawAssociationsInList(process.getArtifacts(), model);
    }
  }

  protected void drawSequenceFlowsInList(Collection<FlowElement> flowList, BpmnMemoryModel model) {
    for (FlowElement flowElement : flowList) {

      if (flowElement instanceof SubProcess) {
        drawSequenceFlowsInList(((SubProcess) flowElement).getFlowElements(), model);
        drawAssociationsInList(((SubProcess) flowElement).getArtifacts(), model);

      } else if (flowElement instanceof SequenceFlow == false) {
        continue;
      } else {
        SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
        drawSequenceFlow(sequenceFlow, model);
      }
    }
  }

  protected void drawSequenceFlow(SequenceFlow sequenceFlow, BpmnMemoryModel model) {
    Anchor sourceAnchor = null;
    Anchor targetAnchor = null;
    ContainerShape sourceShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(
            model.getFlowElement(sequenceFlow.getSourceRef()));

    if (sourceShape == null) {
      return;
    }

    EList<Anchor> anchorList = sourceShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        sourceAnchor = anchor;
        break;
      }
    }

    ContainerShape targetShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(
            model.getFlowElement(sequenceFlow.getTargetRef()));

    if (targetShape == null) {
      return;
    }

    anchorList = targetShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        targetAnchor = anchor;
        break;
      }
    }

    AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);

    List<GraphicInfo> bendpointList = new ArrayList<GraphicInfo>();
    if (model.getBpmnModel().getFlowLocationMap().containsKey(sequenceFlow.getId())) {
      List<GraphicInfo> pointList = model.getBpmnModel().getFlowLocationGraphicInfo(sequenceFlow.getId());
      if (pointList.size() > 2) {
        for (int i = 1; i < pointList.size() - 1; i++) {
          bendpointList.add(pointList.get(i));
        }
      }
    }
    addContext.putProperty("org.activiti.designer.bendpoints", bendpointList);
    addContext.putProperty("org.activiti.designer.connectionlabel", model.getBpmnModel().getLabelGraphicInfo(sequenceFlow.getId()));

    addContext.setNewObject(sequenceFlow);
    getDiagramTypeProvider().getFeatureProvider().addIfPossible(addContext);
  }

  protected void drawAssociationsInList(Collection<Artifact> artifactList, BpmnMemoryModel model) {
    for (Artifact artifact : artifactList) {

      if (artifact instanceof Association == false) {
        continue;
      } else {
        Association association = (Association) artifact;
        drawAssociation(association, model);
      }
    }
  }

  protected void drawAssociation(Association association, BpmnMemoryModel model) {
    Anchor sourceAnchor = null;
    Anchor targetAnchor = null;
    BaseElement sourceElement = model.getFlowElement(association.getSourceRef());
    if (sourceElement == null) {
      sourceElement = model.getArtifact(association.getSourceRef());
    }
    if (sourceElement == null) {
      return;
    }
    ContainerShape sourceShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(sourceElement);

    if (sourceShape == null) {
      return;
    }

    EList<Anchor> anchorList = sourceShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        sourceAnchor = anchor;
        break;
      }
    }

    BaseElement targetElement = model.getFlowElement(association.getTargetRef());
    if (targetElement == null) {
      targetElement = model.getArtifact(association.getTargetRef());
    }
    if (targetElement == null) {
      return;
    }
    ContainerShape targetShape = (ContainerShape) getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(targetElement);

    if (targetShape == null) {
      return;
    }

    anchorList = targetShape.getAnchors();
    for (Anchor anchor : anchorList) {
      if (anchor instanceof ChopboxAnchor) {
        targetAnchor = anchor;
        break;
      }
    }

    AddConnectionContext addContext = new AddConnectionContext(sourceAnchor, targetAnchor);

    List<GraphicInfo> bendpointList = new ArrayList<GraphicInfo>();
    if (model.getBpmnModel().getFlowLocationMap().containsKey(association.getId())) {
      List<GraphicInfo> pointList = model.getBpmnModel().getFlowLocationGraphicInfo(association.getId());
      if (pointList.size() > 2) {
        for (int i = 1; i < pointList.size() - 1; i++) {
          bendpointList.add(pointList.get(i));
        }
      }
    }

    addContext.putProperty("org.activiti.designer.bendpoints", bendpointList);

    addContext.setNewObject(association);
    getDiagramTypeProvider().getFeatureProvider().addIfPossible(addContext);
  }

  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    // hides grid on diagram, but you can reenable it
    if (getGraphicalViewer() != null && getGraphicalViewer().getEditPartRegistry() != null) {
      ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) getGraphicalViewer().getEditPartRegistry().get(LayerManager.ID);
      IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      gridFigure.setVisible(false);
    }
  }

  public static GraphicalViewer getActiveGraphicalViewer() {
    return activeGraphicalViewer;
  }

  @Override
  public void dispose() {
    super.dispose();

    final ActivitiDiagramEditorInput adei = (ActivitiDiagramEditorInput) getEditorInput();

    ModelHandler.removeModel(EcoreUtil.getURI(getDiagramTypeProvider().getDiagram()));
    Bpmn2DiagramCreator.dispose(adei.getDiagramFile());
  }
}
