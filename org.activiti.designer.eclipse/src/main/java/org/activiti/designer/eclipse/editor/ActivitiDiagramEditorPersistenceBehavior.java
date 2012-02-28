package org.activiti.designer.eclipse.editor;

import java.util.Collection;

import org.activiti.designer.eclipse.Logger;
import org.activiti.designer.eclipse.extension.export.ExportMarshaller;
import org.activiti.designer.eclipse.extension.export.SequenceFlowSynchronizer;
import org.activiti.designer.eclipse.ui.ExportMarshallerRunnable;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


public class ActivitiDiagramEditorPersistenceBehavior extends DefaultPersistencyBehavior {

  public ActivitiDiagramEditorPersistenceBehavior(DiagramEditor diagramEditor) {
    super(diagramEditor);
  }

  @Override
  public void saveDiagram(IProgressMonitor monitor) {
    final IEditorPart editorPart = diagramEditor.getEditorSite().getPage().getActiveEditor();
    
    final Diagram diagram = (Diagram) editorPart.getAdapter(Diagram.class);
    
    SequenceFlowSynchronizer.synchronize(diagram.getConnections(), (DiagramEditor) editorPart);

    // Regular save
    try {
      super.saveDiagram(monitor);
    } catch(Throwable e) {
      e.printStackTrace();
    }

    // Determine list of ExportMarshallers to invoke after regular save
    final Collection<ExportMarshaller> marshallers = ExtensionPointUtil
        .getActiveExportMarshallers();

    if (marshallers.size() > 0) {
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

  
}