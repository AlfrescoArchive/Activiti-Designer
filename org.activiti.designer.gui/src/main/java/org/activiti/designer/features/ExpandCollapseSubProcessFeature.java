package org.activiti.designer.features;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.activiti.designer.eclipse.common.FileService;
import org.activiti.designer.eclipse.preferences.PreferencesUtil;
import org.activiti.designer.eclipse.util.Util;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.impl.SubProcessImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.AbstractDrillDownFeature;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;

public class ExpandCollapseSubProcessFeature extends AbstractDrillDownFeature {

	private String subprocessId = null;
	private String subprocessName = null;

	public ExpandCollapseSubProcessFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Expand/Collapse Sub Process"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "Expand or collapse the sub process"; //$NON-NLS-1$
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return ActivitiUiUtil.contextPertainsToBusinessObject(context, SubProcessImpl.class);
	}

	public void execute(ICustomContext context) {
		try {
			SubProcess subprocess = (SubProcess) ActivitiUiUtil.getBusinessObjectFromContext(context,
					SubProcessImpl.class);
			this.subprocessId = subprocess.getId();
			this.subprocessName = subprocess.getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.execute(context);
	}

	@Override
	protected Collection<Diagram> getLinkedDiagrams(PictogramElement pe) {
		return getDiagrams();
	}

	@Override
	protected Collection<Diagram> getDiagrams() {

		Collection<Diagram> result = new ArrayList<Diagram>();
		Resource resource = getDiagram().eResource();

		URI uri = resource.getURI();
		URI uriTrimmed = uri.trimFragment();

		if (uriTrimmed.isPlatformResource()) {

			String platformString = uriTrimmed.toPlatformString(true);

			IResource fileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);

			if (fileResource != null) {
				IProject project = fileResource.getProject();

				final IFile targetFile = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(new Path(Util.getSubProcessURI(getDiagram(), subprocessId).toPlatformString(true)));

				// final IFile targetFile = project.getFile();

				if (targetFile.exists()) {
					result.add(getExistingDiagram(project, targetFile));
				} else {
					result.add(getNewDiagram(project, targetFile));
				}
			}
		}

		return result;
	}

	private Diagram getNewDiagram(final IProject project, final IFile targetFile) {
		Diagram diagram = null;
		URI uri = URI.createPlatformResourceURI(targetFile.getFullPath().toString(), true);

		TransactionalEditingDomain domain = null;

		boolean createContent = PreferencesUtil
				.getBooleanPreference(Preferences.EDITOR_ADD_DEFAULT_CONTENT_TO_DIAGRAMS);

		if (createContent) {
			final InputStream contentStream = Util.getContentStream(Util.Content.NEW_SUBPROCESS_CONTENT);
			InputStream replacedStream = Util.swapStreamContents(subprocessName, contentStream);
			domain = FileService.createEmfFileForDiagram(uri, null, replacedStream, targetFile);
			diagram = org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal.getEmfService().getDiagramFromFile(
					targetFile, domain.getResourceSet());
		} else {
			diagram = Graphiti.getPeCreateService().createDiagram("BPMNdiagram", subprocessName, true);
			domain = FileService.createEmfFileForDiagram(uri, diagram, null, null);
		}

		return diagram;
	}

	private Diagram getExistingDiagram(final IProject project, final IFile targetFile) {
		final ResourceSet rSet = new ResourceSetImpl();
		Diagram diagram = GraphitiUiInternal.getEmfService().getDiagramFromFile(targetFile, rSet);
		return diagram;
	}
}
