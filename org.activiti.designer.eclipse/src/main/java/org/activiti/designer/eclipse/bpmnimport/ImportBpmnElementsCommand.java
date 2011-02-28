package org.activiti.designer.eclipse.bpmnimport;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class ImportBpmnElementsCommand extends RecordingCommand {

	private IProject project;
	private TransactionalEditingDomain editingDomain;
	private String diagramName;
	private String bpmnFileName;
	private Resource createdResource;
	private Diagram diagram;

	public ImportBpmnElementsCommand(IProject project, TransactionalEditingDomain editingDomain, 
	        String diagramName, String bpmnFileName) {
		super(editingDomain);
		this.project = project;
		this.editingDomain = editingDomain;
		this.diagramName = diagramName;
		this.bpmnFileName = bpmnFileName;
	}

	@Override
	protected void doExecute() {
	  // Create the diagram and its file
    diagram = Graphiti.getPeCreateService().createDiagram("BPMNdiagram", diagramName, true); //$NON-NLS-1$
    IFolder diagramFolder = project.getFolder(ActivitiBPMNDiagramConstants.DIAGRAM_FOLDER); //$NON-NLS-1$
    IFile diagramFile = diagramFolder.getFile(diagramName + ".activiti"); //$NON-NLS-1$
    URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
    createdResource = editingDomain.getResourceSet().createResource(uri);
    createdResource.getContents().add(diagram);
    
	  IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram,
        "org.activiti.designer.diagram.ActivitiBPMNDiagramTypeProvider"); //$NON-NLS-1$
    IFeatureProvider featureProvider = dtp.getFeatureProvider();
    BpmnFileReader bpmnFileReader = new BpmnFileReader(bpmnFileName, diagram, featureProvider);
    bpmnFileReader.readBpmn();
	}

	/**
	 * @return the createdResource
	 */
	public Resource getCreatedResource() {
		return createdResource;
	}
	
	public Diagram getDiagram() {
	  return diagram;
	}
}
