package org.activiti.designer.eclipse.editor;

import java.util.Collection;
import java.util.List;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.extension.export.SequenceFlowSynchronizer;
import org.activiti.designer.eclipse.ui.ActivitiEditorContextMenuProvider;
import org.activiti.designer.eclipse.ui.ExportMarshallerRunnable;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.eclipse.bpmn2.Task;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class ActivitiDiagramEditor extends DiagramEditor {

	public final static String ID = "org.activiti.designer.diagmrameditor"; //$NON-NLS-1$
	private static GraphicalViewer activeGraphicalViewer;

	public ActivitiDiagramEditor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
	  super.init(site, input);
	}
	
	@Override
	public void createPartControl(Composite parent) {
	  super.createPartControl(parent);
	  GraphicalViewer graphicalViewer = (GraphicalViewer) getAdapter(GraphicalViewer.class);
    if (graphicalViewer != null && graphicalViewer.getEditPartRegistry() != null) {
      ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
      IFigure gridFigure = ((LayerManager) rootEditPart).getLayer(LayerConstants.GRID_LAYER);
      gridFigure.setVisible(false);
    }
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class type) {
		return super.getAdapter(type);
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		// Get a reference to the editor part

		final IEditorPart editorPart = getEditorSite().getPage().getActiveEditor();
		final DiagramEditorInput editorInput;

		editorInput = (DiagramEditorInput) editorPart.getEditorInput();
		SequenceFlowSynchronizer.synchronize(editorInput.getDiagram()
				.getConnections(), (DiagramEditor) editorPart);

		activeGraphicalViewer = (GraphicalViewer) getAdapter(GraphicalViewer.class);

		// Regular save
		try {
		  super.doSave(monitor);
		} catch(Throwable e) {
		  e.printStackTrace();
		}

		// Determine list of ExportMarshallers to invoke after regular save
		final Collection<ExportMarshaller> marshallers = ExtensionPointUtil
				.getActiveExportMarshallers();

		if (marshallers.size() > 0) {
			// Get the resource belonging to the editor part
			final Diagram diagram = editorInput.getDiagram();

			// Get the progress service so we can have a progress monitor
			final IProgressService progressService = PlatformUI.getWorkbench()
					.getProgressService();

			try {
				final ExportMarshallerRunnable runnable = new ExportMarshallerRunnable(
						diagram, marshallers);
				progressService.busyCursorWhile(runnable);
			} catch (Exception e) {
				Logger.logError("Exception while performing save", e);
			}
		}
	}

	@Override
  protected void setInput(final IEditorInput input) {
		super.setInput(input);
	  if(input instanceof DiagramEditorInput) {
	  	final Diagram diagram = ((DiagramEditorInput) input).getDiagram();
	  	boolean shouldMigrate = Graphiti.getMigrationService().shouldMigrate070To080(diagram);
	  	if(shouldMigrate) {
	  		ActivitiUiUtil.runModelChange(new Runnable() {
					public void run() {
						setTransparencyAndLineWidth(diagram, diagram);
						Graphiti.getMigrationService().migrate070To080(diagram);
					}
				}, getEditingDomain(), "Mirgation from Graphiti 0.7 to 0.8");
	  	}
	  }
  }
	
	private void setTransparencyAndLineWidth(ContainerShape parent, Diagram diagram) {
		List<Shape> shapes = parent.getChildren();
		for (Shape shape : shapes) {
      if(shape instanceof ContainerShape) {
      	GraphicsAlgorithm graphics = ((ContainerShape) shape).getGraphicsAlgorithm();
      	graphics.setLineWidth(1);
      	graphics.setTransparency(0.0);
      	List<GraphicsAlgorithm> graphicsChildren = graphics.getGraphicsAlgorithmChildren();
      	for (GraphicsAlgorithm graphicsAlgorithm : graphicsChildren) {
          if(graphicsAlgorithm.getLineWidth() == null ||  graphicsAlgorithm.getLineWidth() <= 1) {
          	graphicsAlgorithm.setLineWidth(1);
          }
          graphicsAlgorithm.setTransparency(0.0);
        }
      	setTransparencyAndLineWidth((ContainerShape) shape, diagram);
      
      } else if(shape.getGraphicsAlgorithm() != null && shape.getGraphicsAlgorithm() instanceof Text) {
      	if(parent.getLink().getBusinessObjects() != null && parent.getLink().getBusinessObjects().size() > 0) {
      		EObject object = parent.getLink().getBusinessObjects().get(0);
      		if(object instanceof Task) {
      			Text text = (Text) shape.getGraphicsAlgorithm();
          	MultiText multiText = Graphiti.getGaService().createDefaultMultiText(diagram, shape, text.getValue());
          	multiText.setStyle(text.getStyle());
          	multiText.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
          	multiText.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
          	multiText.setFont(text.getFont());
          	multiText.setX(text.getX());
          	multiText.setY(text.getY());
          	multiText.setHeight(30);
          	multiText.setWidth(text.getWidth());
          	shape.setGraphicsAlgorithm(multiText);
      		}
      	}
      }
    }
	}

	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new ActivitiEditorContextMenuProvider(getGraphicalViewer(),
				getActionRegistry(), getConfigurationProvider());
	}

	public static GraphicalViewer getActiveGraphicalViewer() {
		return activeGraphicalViewer;
	}
}
