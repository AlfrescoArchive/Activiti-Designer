/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.eclipse.outline;

import org.activiti.designer.eclipse.common.ISampleImageConstants;
import org.activiti.designer.eclipse.outline.tree.PictogramsTreeEditPartFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.internal.fixed.FixedScrollableThumbnail;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;

/**
 * An outline page for the graphical modeling editor. It displays the contents
 * of the editor either as a hierarchical Outline or as a graphical Thumbnail.
 * There are buttons to switch between those displays. Subclasses should
 * overwrite this outline page (and dependent classes), to change the
 * default-behaviour.
 */
// The generic outline uses internal functionality of Graphiti. For concrete
// tool implementations this should not be necessary
public class GraphicsEditorOutlinePage extends ContentOutlinePage implements IAdaptable, IPropertyListener {

	// The IDs to identify the outline and the thunbnail
	private static final int ID_OUTLINE = 0;

	private static final int ID_THUMBNAIL = 1;

	// Common instances of different Editors/Views, to synchronize their
	// behaviour
	private GraphicalViewer _graphicalViewer;

	private ActionRegistry _actionRegistry;

	private EditDomain _editDomain;

	private KeyHandler _keyHandler;

	private Object _zoomManagerAdapter;

	private SelectionSynchronizer _selectionSynchronizer;

	private DiagramEditor _diagramEditor;

	// The thumbnail to display
	private FixedScrollableThumbnail _thumbnail;

	// Actions (buttons) to switch between outline and overview
	private IAction _showOutlineAction;

	private IAction _showOverviewAction;

	// The pagebook, which displays either the outline or the overview
	private PageBook _pageBook;

	// The outline-control and the thumbnail-control of the pagebook
	private Control _outline;

	private Canvas _overview;

	/**
	 * Creates a new GraphicsEditorOutlinePage. It is important, that this
	 * outline page uses the same handlers (ActionRegistry, KeyHandler,
	 * ZoomManagerAdapter, ...) as the main editor, so that the behaviour is
	 * synchronized between them.
	 * 
	 * @param viewer
	 *            The viewer (typically a tree-viewer) for the hierarchical
	 *            outline.
	 * @param graphicalViewer
	 *            The GraphicalViewer for the Thumbnail.
	 * @param actionRegistry
	 *            The ActionRegistry to find/register Actions.
	 * @param editDomain
	 *            The EditDomain to use for Commands.
	 * @param keyHandler
	 *            The KeyHandler to use.
	 * @param zoomManagerAdapter
	 *            The ZoomManagerAdapter to use for the Thumbnail-Display.
	 * @param selectionSynchronizer
	 *            The selection-synchronizer for the main-editor and this
	 *            outline page.
	 * @param configurationProviderHolder
	 *            the configuration provider holder
	 */
	public GraphicsEditorOutlinePage(EditPartViewer viewer, GraphicalViewer graphicalViewer, ActionRegistry actionRegistry,
			EditDomain editDomain, KeyHandler keyHandler, Object zoomManagerAdapter, SelectionSynchronizer selectionSynchronizer,
			DiagramEditor diagramEditor) {
		super(viewer);
		_graphicalViewer = graphicalViewer;
		_actionRegistry = actionRegistry;
		_editDomain = editDomain;
		_keyHandler = keyHandler;
		_zoomManagerAdapter = zoomManagerAdapter;
		_selectionSynchronizer = selectionSynchronizer;
		_diagramEditor = diagramEditor;
	}

	// ========================= standard behaviour ===========================

	/**
	 * Is called to indicate, that the contents have changed. Causes a complete
	 * update of the contents of the outline page.
	 */
	public void initContents() {
		EditPartFactory treeEditPartFactory = new PictogramsTreeEditPartFactory();
		getViewer().setEditPartFactory(treeEditPartFactory);
		Diagram diagram = ((DiagramEditorInput) _diagramEditor.getEditorInput()).getDiagram();
		getViewer().setContents(diagram);
	}

	/**
	 * Is used to register several global action handlers (UNDO, REDO, COPY,
	 * PASTE, ...) on initialization of this outline page. This activates for
	 * example the undo-action in the central Eclipse-Menu.
	 * 
	 * @param pageSite
	 *            the page site
	 * 
	 * @see org.eclipse.ui.part.Page#init(IPageSite)
	 */
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		IActionBars actionBars = pageSite.getActionBars();
		registerGlobalActionHandler(actionBars, ActionFactory.UNDO.getId());
		registerGlobalActionHandler(actionBars, ActionFactory.REDO.getId());
		registerGlobalActionHandler(actionBars, ActionFactory.COPY.getId());
		registerGlobalActionHandler(actionBars, ActionFactory.PASTE.getId());
		registerGlobalActionHandler(actionBars, ActionFactory.PRINT.getId());
		registerGlobalActionHandler(actionBars, ActionFactory.SAVE_AS.getId());
		actionBars.updateActionBars();
	}

	/**
	 * Creates the Control of this outline page. By default this is a PageBook,
	 * which can toggle between a hierarchical Outline and a graphical
	 * Thumbnail.
	 * 
	 * @param parent
	 *            the parent
	 * 
	 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		_pageBook = new PageBook(parent, SWT.NONE);
		_outline = getViewer().createControl(_pageBook);
		_overview = new Canvas(_pageBook, SWT.NONE);
		_pageBook.showPage(_outline);
		createOutlineViewer();

		// register listeners
		_selectionSynchronizer.addViewer(getViewer());
		_diagramEditor.addPropertyListener(this);

		initContents();
	}

	/**
	 * Deregisters all 'listeners' of the main-editor.
	 */
	@Override
	public void dispose() {
		// deregister listeners
		_selectionSynchronizer.removeViewer(getViewer());
		_diagramEditor.removePropertyListener(this);

		if (_thumbnail != null)
			_thumbnail.deactivate();

		super.dispose();
	}

	/**
	 * Returns the Control of this outline page, which was created in
	 * createControl().
	 * 
	 * @return the control
	 * 
	 * @see org.eclipse.gef.ui.parts.ContentOutlinePage#getControl()
	 */
	@Override
	public Control getControl() {
		return _pageBook;
	}

	/**
	 * This implementation returns the ZoomManager for the ZoomManager.class.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @return the adapter
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#getAdapter(Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == ZoomManager.class)
			return _zoomManagerAdapter;
		return null;
	}

	/**
	 * Refreshes the outline on any change of the diagram editor. Most
	 * importantly, there is a property change event editor-dirty.
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		refresh();
	}

	/**
	 * Toggles the page to display between hierarchical Outline and graphical
	 * Thumbnail.
	 * 
	 * @param id
	 *            The ID of the page to display. It must be either ID_OUTLINE or
	 *            ID_THUMBNAIL.
	 */
	protected void showPage(int id) {
		if (id == ID_OUTLINE) {
			_showOutlineAction.setChecked(true);
			_showOverviewAction.setChecked(false);
			_pageBook.showPage(_outline);
		} else if (id == ID_THUMBNAIL) {
			if (_thumbnail == null)
				createThumbnailViewer();
			_showOutlineAction.setChecked(false);
			_showOverviewAction.setChecked(true);
			_pageBook.showPage(_overview);
		}
	}

	/**
	 * Creates the hierarchical Outline viewer.
	 */
	protected void createOutlineViewer() {
		// set the standard handlers
		getViewer().setEditDomain(_editDomain);
		getViewer().setKeyHandler(_keyHandler);

		// add a context-menu
		ContextMenuProvider contextMenuProvider = createContextMenuProvider();
		if (contextMenuProvider != null)
			getViewer().setContextMenu(contextMenuProvider);

		// add buttons outline/overview to toolbar
		IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
		_showOutlineAction = new Action() {

			@Override
			public void run() {
				showPage(ID_OUTLINE);
			}
		};
		_showOutlineAction.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(ISampleImageConstants.IMG_OUTLINE_TREE));
		tbm.add(_showOutlineAction);
		_showOverviewAction = new Action() {

			@Override
			public void run() {
				showPage(ID_THUMBNAIL);
			}
		};
		_showOverviewAction.setImageDescriptor(GraphitiUi.getImageService().getImageDescriptorForId(
				ISampleImageConstants.IMG_OUTLINE_THUMBNAIL));
		tbm.add(_showOverviewAction);

		// by default show the outline-page
		showPage(ID_OUTLINE);
	}

	/**
	 * Returns a new ContextMenuProvider. Can be null, if no context-menu shall
	 * be displayed.
	 * 
	 * @return A new ContextMenuProvider.
	 */
	protected ContextMenuProvider createContextMenuProvider() {
		return null;
	}

	/**
	 * Creates the graphical Thumbnail viewer.
	 */
	protected void createThumbnailViewer() {
		LightweightSystem lws = new LightweightSystem(_overview);
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) _graphicalViewer.getRootEditPart();
		_thumbnail = new FixedScrollableThumbnail((Viewport) rootEditPart.getFigure());
		_thumbnail.setBorder(new MarginBorder(3));
		_thumbnail.setSource(rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));
		lws.setContents(_thumbnail);
	}

	// ========================= private helper methods =======================

	private void registerGlobalActionHandler(IActionBars actionBars, String id) {
		IAction action = _actionRegistry.getAction(id);
		if (action != null)
			actionBars.setGlobalActionHandler(id, action);
	}

	/**
	 * Refresh.
	 */
	void refresh() {
		final EditPartViewer viewer = getViewer();
		final EditPart contents = viewer.getContents();
		if (contents != null) {
			contents.refresh();
		}
	}
}