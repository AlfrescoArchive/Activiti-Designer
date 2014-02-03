package org.activiti.designer.eclipse.views.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activiti.designer.eclipse.editor.ActivitiDiagramEditor;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.activiti.designer.eclipse.util.ExtensionPointUtil;
import org.activiti.designer.util.ActivitiConstants;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * Activiti view providing results and actions for validation and verification
 * of BPMN 2.0 diagrams.
 * 
 * @author Juraj Husar (jurosh@jurosh.com)
 * 
 */
public class ActivitiValidationView extends ViewPart {

	private Button validateButton;
	private Button clearButton;
	private Button configButton;
	private Table table;

	@Override
	public void createPartControl(Composite parent) {

		// Config layout, tutorial: http://zetcode.com/gui/javaswt/layout/
		GridLayout gridLayout = new GridLayout(3, true);
		parent.setLayout(gridLayout);

		// create UI
		Label indevLabel = new Label(parent, SWT.PUSH);
		indevLabel.setText("Validation is currently in BETA. After some develpoment work this sign will be removed.");
		GridData gridData2 = new GridData(GridData.VERTICAL_ALIGN_END);
		gridData2.horizontalSpan = 3;
		indevLabel.setLayoutData(gridData2);

		validateButton = new Button(parent, SWT.PUSH);
		validateButton.setText("Validate");

		clearButton = new Button(parent, SWT.PUSH);
		clearButton.setText("Clear");

		configButton = new Button(parent, SWT.PUSH);
		configButton.setText("Configuration");

		// define the TableViewer
		TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// create the columns
		// not yet implemented
		createColumns(viewer);

		// make lines and header visible
		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(ModelProvider.INSTANCE.getData());

		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_END);
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		table.setLayoutData(gridData);

		resizeTable();
		
		bindActions();
	}

	// inspired by
	// http://www.vogella.com/tutorials/EclipseJFaceTable/article.html
	private void createColumns(TableViewer viewer) {

		createColumn(viewer, "Type", "Type");
		createColumn(viewer, "Element", "Element1Id");
		createColumn(viewer, "2nd Element", "Element2Id");
		createColumn(viewer, "Message", "Reason");

	}

	/**
	 * 
	 * @param viewer
	 * @param title
	 * @param propertyName
	 *            property name beginning with capital (must be getter in model)
	 * @return
	 */
	private TableViewerColumn createColumn(TableViewer viewer, String title, final String propertyName) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		// column.setWidth(10);
		column.setResizable(true);
		column.setMoveable(true);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null && element instanceof ValidationObject) {
					try {
						Method method = ValidationObject.class.getDeclaredMethod("get" + propertyName);
						Object returns = method.invoke(element);
						return returns == null ? "-" : returns.toString();

					} catch (NoSuchMethodException e) {
						// TODO use logger?
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}

				}
				return "-";
			}
		});
		return viewerColumn;
	}

	private void bindActions() {
		validateButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				validate();
			}
		});
	}

	private void resizeTable() {
		for (TableColumn tc : table.getColumns()) {
			tc.pack();
		}
	}

	protected void validate() {
		System.out.println("RUN VALIDATE!");

		// inspired by AbstractExportMarshaller.invokeValidators
		// final int totalWork = WORK_INVOKE_VALIDATORS_VALIDATOR *
		// validatorIds.size();
		// final IProgressMonitor activeMonitor = monitor == null ? new
		// NullProgressMonitor() : monitor;
		// activeMonitor.beginTask("Invoking validators", totalWork);
		// boolean overallResult = true;

		Iterator<ProcessValidator> iterator = ExtensionPointUtil.getProcessValidators().iterator();
		while (iterator.hasNext()) {
			System.out.print(iterator.next().getValidatorId());
		}

		// get validator, else skip
		final ProcessValidator processValidator = ExtensionPointUtil.getProcessValidator(ActivitiConstants.BPMN_VALIDATOR_ID);

		if (processValidator != null) {
			System.out.println("OK, have validator...");
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	private BpmnMemoryModel getActiveModel() {
		// URI uri = EcoreUtil.getURI(getDiagramTypeProvider().getDiagram());
		// TODO allways get last diagram
		URI uri = EcoreUtil.getURI(ActivitiDiagramEditor.EDITOR.getDiagramTypeProvider().getDiagram());
		return ModelHandler.getModel(uri);
	}

	/**
	 * Model provider
	 * 
	 * @author Jurosh
	 * 
	 */
	public enum ModelProvider {
		INSTANCE;

		private List<ValidationObject> sampleValidationObjects;

		private ModelProvider() {
			sampleValidationObjects = new ArrayList<ValidationObject>();
			// Image here some fancy database access to read the persons and to
			// put them into the model
			sampleValidationObjects.add(new ValidationObject("error", "Start Element", "End element (end1)", "Not connected"));
			sampleValidationObjects.add(new ValidationObject("warning", "Pool", "", "Pool is missing"));
			sampleValidationObjects.add(new ValidationObject("info", "Text", "", "Use text"));
			sampleValidationObjects.add(new ValidationObject("warning", "Las elem (elem1)", "", "This is not good"));
			sampleValidationObjects.add(new ValidationObject("error", "Element5", "", "Erorr"));
		}

		public List<ValidationObject> getData() {
			return sampleValidationObjects;
		}

	}

	/**
	 * Validation result object
	 * 
	 * @author Jurosh
	 * 
	 */
	static class ValidationObject {

		private String type;
		private String element1Id;
		private String element2Id;
		private String reason;

		public ValidationObject(String type, String element1Id, String element2Id, String reason) {
			super();
			this.type = type;
			this.element1Id = element1Id;
			this.element2Id = element2Id;
			this.reason = reason;
		}

		@Override
		public String toString() {
			return type + " " + reason;
		}

		public String getType() {
			return type;
		}

		public String getElement1Id() {
			return element1Id;
		}

		public String getElement2Id() {
			return element2Id;
		}

		public String getReason() {
			return reason;
		}

	}

}
