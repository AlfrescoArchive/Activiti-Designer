package org.activiti.designer.features;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Provides update feature that updates the business model for connections if
 * the graphical model points to a different source or target object than the
 * corresponding flow in the business model. This is an unfortunate necessity
 * because for some reason, relinking the connector doesn't trigger a business
 * model update (probably a bug in Graphiti or the way it's used). Assuming this
 * will be fixed within the Graphiti framework in a later version, this is
 * marked as deprecated for later deletion.
 * 
 * @author Tiese Barrell
 * @since 0.5.0
 * @version 1
 * 
 */
@Deprecated
public class UpdateConnectionFlowElementFeature extends AbstractUpdateFeature {

	private SyncObjects syncObjects;

	public UpdateConnectionFlowElementFeature(IFeatureProvider fp) {
		super(fp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.func.IUpdate#canUpdate(org.eclipse.graphiti.features
	 * .context.IUpdateContext)
	 */
	public boolean canUpdate(IUpdateContext context) {
		// return true, if linked business object is an SequenceFlow
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof SequenceFlow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.graphiti.func.IUpdate#updateNeeded(org.eclipse.graphiti.features
	 * .context.IUpdateContext)
	 */
	public IReason updateNeeded(IUpdateContext context) {

		final PictogramElement pictogramElement = context.getPictogramElement();
		final Object bo = getBusinessObjectForPictogramElement(pictogramElement);

		this.syncObjects = new SyncObjects(context);

		//TODO: reinstate this block, but only for updateable connectors to automatically update if needed so the user is not required to do this manually
		// possibly, #update() should be expanded to detect duplicate updates if the user still forces it and updating is no longer required. This depends on the lifecycle of this 
		//instance and would entail creating a force override of the #isOutOfSync() method's result to reflect having been manually updated.
//		if (bo instanceof SequenceFlow && isOutOfSync(context)) {
//			// manually force an update
//			// FIXME: need transaction that's writable
//			TransactionalEditingDomain editingDomain = getDiagramEditor().getEditingDomain();
//			CommandExec.getSingleton().executeFeatureWithContext(this, context);
//		}
		
		//FIXME: this is far from perfect, but limits red dashed connectors to updateable connectors only
		if (isOutOfSync(context)) {
			return Reason.createTrueReason("This sequence flow requires updating because the source and \ndestination nodes have changed since the diagram was last saved. \n\nUse right-click > Update to update the sequence flow");	
		}
		return Reason.createFalseReason();
	}

	public boolean update(IUpdateContext context) {
		this.syncObjects = new SyncObjects(context);
		return this.syncObjects.update();
	}

	private boolean isOutOfSync(final IUpdateContext context) {
		return this.syncObjects.isOutOfSync();
	}

	private class SyncObjects {

		private String graphicalStartId;
		private String graphicalEndId;
		private String businessStartId;
		private String businessEndId;

		private FlowElement targetStartObject;
		private FlowElement targetEndObject;
		private SequenceFlow businessModelObjectToUpdate;

		private final IUpdateContext context;

		public SyncObjects(final IUpdateContext context) {
			this.context = context;
			setup();
		}

		private void setup() {

			final PictogramElement pictogramElement = context.getPictogramElement();
			final Object bo = getBusinessObjectForPictogramElement(pictogramElement);

			if (pictogramElement instanceof Connection) {

				final Connection anchor = (Connection) pictogramElement;

				final Object startPE = getBusinessObjectForPictogramElement(anchor.getStart().getParent().getLink()
						.getPictogramElement());

				if (startPE instanceof FlowElement) {
					targetStartObject = (FlowElement) startPE;
					graphicalStartId = targetStartObject.getId();
				}

				final Object endPE = getBusinessObjectForPictogramElement(anchor.getEnd().getParent().getLink()
						.getPictogramElement());

				if (endPE instanceof FlowElement) {
					targetEndObject = (FlowElement) endPE;
					graphicalEndId = targetEndObject.getId();
				}

				businessModelObjectToUpdate = ((SequenceFlow) bo);

				businessStartId = businessModelObjectToUpdate.getSourceRef().getId();
				businessEndId = businessModelObjectToUpdate.getTargetRef().getId();

			}
		}

		public boolean isOutOfSync() {

			if (!graphicalStartId.equals(businessStartId) || !graphicalEndId.equals(businessEndId)) {
				return true;
			}

			return false;
		}

		public boolean update() {
			if (isOutOfSync()) {

				System.out
						.println(String
								.format("Updating because the pictogram elements point from %s to %s and the business objects point from %s to %s",
										graphicalStartId, graphicalEndId, businessStartId, businessEndId));

				businessModelObjectToUpdate.setSourceRef((FlowNode) targetStartObject);
				businessModelObjectToUpdate.setTargetRef((FlowNode) targetEndObject);
				return true;
			}
			return false;
		}
	}

}
