package org.activiti.designer.kickstart.eclipse.editor;

import org.activiti.designer.kickstart.eclipse.util.FileService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * Class capable of creating new, editable form-diagrams, exposed as a
 * {@link KickstartDiagramEditorInput}.
 * 
 * @author Frederik Heremans
 * 
 */
public class KickstartFormDiagramCreator {

  public KickstartDiagramEditorInput createFormDiagram(final IFile dataFile, final IFile diagramFile,
      final KickstartFormEditor diagramEditor) {

    IFile finalDataFile = dataFile;

    final IPath diagramPath = diagramFile.getFullPath();
    final String diagramName = diagramPath.removeFileExtension().lastSegment();
    final URI uri = URI.createPlatformResourceURI(diagramPath.toString(), true);

    final Diagram diagram = Graphiti.getPeCreateService().createDiagram("KickstartFormDiagram", diagramName, true);

    FileService.createEmfFileForDiagram(uri, diagram, diagramEditor, null, null);

    final String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
    final KickstartDiagramEditorInput result = new KickstartDiagramEditorInput(EcoreUtil.getURI(diagram), providerId);
    result.setDataFile(finalDataFile);
    result.setDiagramFile(diagramFile);
    return result;
  }
}
