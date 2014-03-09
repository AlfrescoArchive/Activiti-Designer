package org.activiti.designer.eclipse.views.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.eclipse.editor.ActivitiDiagramEditor;
import org.activiti.designer.eclipse.extension.validation.ProcessValidator;
import org.activiti.designer.eclipse.extension.validation.ValidationResults;
import org.activiti.designer.eclipse.extension.validation.ValidationResults.ValidationResult;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


// just for window builder testing
@Deprecated
public class CopyOfActivitiValidationView extends ViewPart {
	public CopyOfActivitiValidationView() {
	}

	private Button validateButton;
	private Button clearButton;
	private Button configButton;
	private Table table;
	
	private Text text;

	private List<ValidationResult> data = new ArrayList<ValidationResults.ValidationResult>();
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		// create UI
		Label indevLabel = new Label(parent, SWT.PUSH);
		indevLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 3, 1));
		indevLabel.setText("Validation is currently in BETA. After some develpoment work this sign will be removed.");

		validateButton = new Button(parent, SWT.PUSH);
		validateButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		validateButton.setText("Validate");

		clearButton = new Button(parent, SWT.PUSH);
		clearButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
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
		GridData gd_table = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 3, 1);
		gd_table.heightHint = 67;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(data);

		resizeTable();
	    text = new Text(parent, SWT.BORDER | SWT.V_SCROLL);
	    GridData gd_text = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 3, 1);
	    gd_text.heightHint = 126;
	    gd_text.widthHint = 172;
	    text.setLayoutData(gd_text);
		
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
				if (element != null && element instanceof ValidationResult) {
					try {
						Method method = ValidationResult.class.getDeclaredMethod("get" + propertyName);
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
	
	// TODO refresh correctly...
	private void refreshTable() {
		table.redraw();
	}

	protected void validate() {
		System.out.println("RUN VALIDATE!");
		data.clear();

		// inspired by AbstractExportMarshaller.invokeValidators
		// final int totalWork = WORK_INVOKE_VALIDATORS_VALIDATOR *
		// validatorIds.size();
		// final IProgressMonitor activeMonitor = monitor == null ? new
		// NullProgressMonitor() : monitor;
		// activeMonitor.beginTask("Invoking validators", totalWork);
		// boolean overallResult = true;

		// get validator, else skip
		final ProcessValidator processValidator = ExtensionPointUtil.getProcessValidator(ActivitiConstants.BPMN_VALIDATOR_ID);
		if (processValidator != null) {
			System.out.println("OK, have validator...");
			// TODO correct way of getting ACTIVE diagram
			ValidationResults validateDiagram = processValidator.validateDiagram(ActivitiDiagramEditor.EDITOR.getDiagramTypeProvider().getDiagram());
			data.addAll(validateDiagram.getResults());
			
			System.out.println("Validation DONE!");
			textClear();
			for (ValidationResult result : data) {
				textAppend(result.getReason());
			}
			
			refreshTable();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	
	private void textClear() {
		text.setText("");
	}

	private void textAppend(String line) {
		text.append("Line " + line + "\n");
		/*
		 * shell.open(); while (!shell.isDisposed()) { if
		 * (!display.readAndDispatch()) display.sleep(); } display.dispose();
		 */
	}

	@Deprecated
	private BpmnMemoryModel getActiveModel() {
		// URI uri = EcoreUtil.getURI(getDiagramTypeProvider().getDiagram());
		// TODO allways get last diagram
		URI uri = EcoreUtil.getURI(ActivitiDiagramEditor.EDITOR.getDiagramTypeProvider().getDiagram());
		return ModelHandler.getModel(uri);
	}

//	public enum ModelProvider {
//		INSTANCE;
//
//		private List<ValidationObject> sampleValidationObjects;
//
//		private ModelProvider() {
//			sampleValidationObjects = new ArrayList<ValidationObject>();
//			// Image here some fancy database access to read the persons and to
//			// put them into the model
//			sampleValidationObjects.add(new ValidationObject("error", "Start Element", "End element (end1)", "Not connected"));
//			sampleValidationObjects.add(new ValidationObject("warning", "Pool", "", "Pool is missing"));
//			sampleValidationObjects.add(new ValidationObject("info", "Text", "", "Use text"));
//			sampleValidationObjects.add(new ValidationObject("warning", "Las elem (elem1)", "", "This is not good"));
//			sampleValidationObjects.add(new ValidationObject("error", "Element5", "", "Erorr"));
//		}
//
//		public List<ValidationObject> getData() {
//			return sampleValidationObjects;
//		}
//
//	}

}
