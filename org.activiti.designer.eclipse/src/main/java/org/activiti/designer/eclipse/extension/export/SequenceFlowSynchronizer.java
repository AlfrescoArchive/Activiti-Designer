package org.activiti.designer.eclipse.extension.export;

import java.util.List;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.ui.editor.DiagramEditor;

/**
 * Utility class to synchronize differences between the graphical representation and business model objects of a
 * diagram.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class SequenceFlowSynchronizer {

	private SequenceFlowSynchronizer() {
	}

	/**
	 * Synchronizes the provided list of {@link Connection}s by inspecting any differences between the graphical
	 * representation and business model objects. For any {@link Connection} where the ids of corresponding graphical
	 * and business objects don't match, the business objects are relinked in the model so they accurately reflect the
	 * graphical representation.
	 * 
	 * @param connections
	 *            the list of {@link Connection} objects to inspect. In most cases, this list would contain all
	 *            connections of the diagram
	 * @param diagramEditor
	 *            the {@link DiagramEditor} of which the diagram is currently being inspected
	 */
	public static void synchronize(final List<Connection> connections, final DiagramEditor diagramEditor) {

		for (final Connection connection : connections) {

			final SyncObjects objects = new SyncObjects(connection, diagramEditor);

			if (objects.isOutOfSync()) {
				objects.update();
			}
		}

	}

	private static final class SyncObjects {

		private final DiagramEditor diagramEditor;

		private String graphicalStartId;
		private String graphicalEndId;
		private String businessStartId;
		private String businessEndId;

		private FlowElement targetStartObject;
		private FlowElement targetEndObject;
		private SequenceFlow businessModelObjectToUpdate;
		private Connection graphicalRepresentation;

		protected SyncObjects(final Connection connection, final DiagramEditor diagramEditor) {
			this.graphicalRepresentation = connection;
			this.diagramEditor = diagramEditor;
			setup();
		}

		private void setup() {
		  
		  if(this.graphicalRepresentation == null) return;
		  
      if(this.graphicalRepresentation.getStart() == null) return;
		  
		  if(this.graphicalRepresentation.getStart().getParent() == null) return;
		  
		  if(this.graphicalRepresentation.getStart().getParent().getLink().getBusinessObjects().size() == 0) return;
		  
			final EObject startPE = this.graphicalRepresentation.getStart().getParent().getLink().getBusinessObjects().get(0);
			targetStartObject = (FlowElement) startPE;
			graphicalStartId = targetStartObject.getId();
      
      if(this.graphicalRepresentation.getEnd() == null) return;
      
      if(this.graphicalRepresentation.getEnd().getParent() == null) return;
      
      if(this.graphicalRepresentation.getEnd().getParent().getLink().getBusinessObjects().size() == 0) return;
      
			final EObject endPE = this.graphicalRepresentation.getEnd().getParent().getLink().getBusinessObjects().get(0);
			targetEndObject = (FlowElement) endPE;
			graphicalEndId = targetEndObject.getId();

			businessModelObjectToUpdate = (SequenceFlow) this.graphicalRepresentation.getLink().getBusinessObjects()
					.get(0);
			
			if(businessModelObjectToUpdate.getSourceRef() != null) {
			  businessStartId = businessModelObjectToUpdate.getSourceRef().getId();
			}
			if(businessModelObjectToUpdate.getTargetRef() != null) {
			  businessEndId = businessModelObjectToUpdate.getTargetRef().getId();
			}
		}

		public boolean isOutOfSync() {
		  
		  if(graphicalStartId == null || graphicalEndId == null) return false;
		  
			if (!graphicalStartId.equals(businessStartId) || !graphicalEndId.equals(businessEndId)) {
				return true;
			}
			return false;
		}

		public boolean update() {
			if (isOutOfSync()) {

				System.out
						.println(String
								.format("Updating because the pictogram elements point from '%s' to '%s', but the business objects point from '%s' to '%s'. The difference must be corrected before saving the diagram",
										graphicalStartId, graphicalEndId, businessStartId, businessEndId));

				TransactionalEditingDomain editingDomain = this.diagramEditor.getEditingDomain();
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain, "ConnectionUpdate") {
					protected void doExecute() {
						businessModelObjectToUpdate.setSourceRef((FlowNode) targetStartObject);
						businessModelObjectToUpdate.setTargetRef((FlowNode) targetEndObject);
					}
				});

				return true;
			}
			return false;
		}
	}

}
