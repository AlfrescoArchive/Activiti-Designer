package org.activiti.designer.eclipse.navigator;

import java.util.HashMap;
import java.util.Map;

import org.activiti.designer.eclipse.common.ActivitiBPMNDiagramConstants;
import org.activiti.designer.eclipse.common.ActivitiProjectNature;
import org.activiti.designer.eclipse.navigator.nodes.BpmnElementsNode;
import org.activiti.designer.eclipse.navigator.nodes.base.IContainerNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.UIJob;

public class BpmnTreeContentProvider implements ITreeContentProvider, IResourceChangeListener {

	private Viewer viewer;
	private Map<IProject, BpmnElementsNode> projectToBpmn2ElementsNode = new HashMap<IProject, BpmnElementsNode>();

	public BpmnTreeContentProvider() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IContainerNode) {
			IContainerNode icn = (IContainerNode) parentElement;
			return icn.getChildren();
		}
		if (parentElement instanceof IProject) {
			IProject project = (IProject) parentElement;
			try {
				if (project.isAccessible() && project.hasNature(ActivitiProjectNature.NATURE_ID)) {
					BpmnElementsNode bpmn2Node = projectToBpmn2ElementsNode.get(project);
					if (bpmn2Node == null) {
						bpmn2Node = new BpmnElementsNode(project, project, viewer);
						projectToBpmn2ElementsNode.put(project, bpmn2Node);
					}
					return new Object[] { bpmn2Node };
				}
			} catch (CoreException e) {
				// Ignore. E.g., project was deleted.
			}
		}
		if (parentElement instanceof EObject) {
			EObject eObject = (EObject) parentElement;
			return eObject.eContents().toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IContainerNode) {
			IContainerNode icn = (IContainerNode) element;
			return icn.hasChildren();
		}
		if (element instanceof EObject) {
			return !((EObject) element).eContents().isEmpty();
		}
		return true;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return null;
	}

	@Override
	public void dispose() {
		// do nothing 
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		try {
			delta.accept(new IResourceDeltaVisitor() {

				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					IResource resource = delta.getResource();
					if (resource == null)
						return false;
					switch (resource.getType()) {
					case IResource.ROOT:
						return true;
					case IResource.PROJECT:
						IProject p = (IProject) resource;
						try {
							boolean hasNature = p.hasNature(ActivitiProjectNature.NATURE_ID);
							return hasNature;
						} catch (CoreException e) {
							// Do nothing, e.g. project deleted.
						}
						return false;
					case IResource.FOLDER:
						return true;
					case IResource.FILE:
						final IFile file = (IFile) resource;
						if (file.getName().endsWith(ActivitiBPMNDiagramConstants.DIAGRAM_EXTENSION) || file.getName().equals("Predefined.data")) { //$NON-NLS-1$
							UIJob job = new UIJob("Update Viewer") { //$NON-NLS-1$
								@Override
								public IStatus runInUIThread(IProgressMonitor monitor) {
									if (viewer != null && !viewer.getControl().isDisposed()) {
										BpmnElementsNode bpmnNode = projectToBpmn2ElementsNode.get(file.getProject());
										if (viewer instanceof StructuredViewer && bpmnNode != null) {
											((StructuredViewer) viewer).refresh(bpmnNode, true);
										} else {
											viewer.refresh();
										}
									}
									return Status.OK_STATUS;
								}
							};
							job.setSystem(true);
							job.schedule();
						}
						return false;
					}
					return false;

				}

			});
		} catch (CoreException e1) {
			return;
		}
	}
}
