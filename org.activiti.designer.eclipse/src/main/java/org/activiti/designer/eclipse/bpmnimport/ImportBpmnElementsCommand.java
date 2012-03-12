package org.activiti.designer.eclipse.bpmnimport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
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

	private TransactionalEditingDomain editingDomain;
	private String diagramName;
	private String bpmnFileName;
	private Resource createdResource;
	private Diagram diagram;
	private IContainer targetFolder;

	public ImportBpmnElementsCommand(TransactionalEditingDomain editingDomain, 
	        String diagramName, String bpmnFileName, IContainer targetFolder) {
		super(editingDomain);
		this.editingDomain = editingDomain;
		this.diagramName = diagramName;
		this.bpmnFileName = bpmnFileName;
		this.targetFolder = targetFolder;
	}

	@Override
	protected void doExecute() {
	  // Create the diagram and its file
    diagram = Graphiti.getPeCreateService().createDiagram("BPMNdiagram", diagramName, true); //$NON-NLS-1$
    IFile diagramFile = targetFolder.getFile(new Path(diagramName + ".activiti")); //$NON-NLS-1$
    URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
    createdResource = editingDomain.getResourceSet().createResource(uri);
    createdResource.getContents().add(diagram);
    
	  IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram,
        "org.activiti.designer.diagram.ActivitiBPMNDiagramTypeProvider"); //$NON-NLS-1$
    IFeatureProvider featureProvider = dtp.getFeatureProvider();
    BpmnFileReader bpmnFileReader = new BpmnFileReader(bpmnFileName, diagram, featureProvider);
    bpmnFileReader.openStream();
    bpmnFileReader.readBpmn(null);
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
