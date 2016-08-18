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
