package org.activiti.designer.eclipse.editor;

import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.graphiti.ui.internal.action.CopyAction;
import org.eclipse.graphiti.ui.internal.action.PasteAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Manages the installation/deinstallation of global actions for multi-page
 * editors. Responsible for the redirection of global actions to the active
 * editor. Multi-page contributor replaces the contributors for the individual
 * editors in the multi-page editor.
 */
public class ActivitiMultiPageEditorContributor extends
		MultiPageEditorActionBarContributor {
  
  private IEditorPart activeEditorPart;

  protected IAction getAction(ActivitiDiagramEditor editor, String actionID) {
    return (editor == null ? null : editor.getActionRegistryInternal().getAction(actionID));
  }
  
  protected IAction getAction(ITextEditor editor, String actionID) {
    return (editor == null ? null : editor.getAction(actionID));
  }
 
  public void setActivePage(IEditorPart part) {
    if (activeEditorPart == part)
      return;

    activeEditorPart = part;

    IActionBars actionBars = getActionBars();
    if (actionBars != null && part instanceof ITextEditor) {
      
      ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

      actionBars.setGlobalActionHandler(
        ActionFactory.DELETE.getId(),
        getAction(editor, ITextEditorActionConstants.DELETE));
      actionBars.setGlobalActionHandler(
        ActionFactory.UNDO.getId(),
        getAction(editor, ITextEditorActionConstants.UNDO));
      actionBars.setGlobalActionHandler(
        ActionFactory.REDO.getId(),
        getAction(editor, ITextEditorActionConstants.REDO));
      actionBars.setGlobalActionHandler(
        ActionFactory.CUT.getId(),
        getAction(editor, ITextEditorActionConstants.CUT));
      actionBars.setGlobalActionHandler(
        ActionFactory.COPY.getId(),
        getAction(editor, ITextEditorActionConstants.COPY));
      actionBars.setGlobalActionHandler(
        ActionFactory.PASTE.getId(),
        getAction(editor, ITextEditorActionConstants.PASTE));
      actionBars.setGlobalActionHandler(
        ActionFactory.SELECT_ALL.getId(),
        getAction(editor, ITextEditorActionConstants.SELECT_ALL));
      actionBars.setGlobalActionHandler(
        ActionFactory.FIND.getId(),
        getAction(editor, ITextEditorActionConstants.FIND));
      actionBars.setGlobalActionHandler(
        IDEActionFactory.BOOKMARK.getId(),
        getAction(editor, IDEActionFactory.BOOKMARK.getId()));
      actionBars.updateActionBars();
      
    } else if (actionBars != null && part instanceof ActivitiDiagramEditor) {
      
      ActivitiDiagramEditor editor = (part instanceof ActivitiDiagramEditor) ? (ActivitiDiagramEditor) part : null;
      
      actionBars.setGlobalActionHandler(
        ActionFactory.DELETE.getId(), editor.getActionRegistryInternal().getAction("delete"));
      actionBars.setGlobalActionHandler(
        ActionFactory.UNDO.getId(),
        getAction(editor, GEFActionConstants.UNDO));
      actionBars.setGlobalActionHandler(
        ActionFactory.REDO.getId(),
        getAction(editor, GEFActionConstants.REDO));
      actionBars.setGlobalActionHandler(
        ActionFactory.COPY.getId(),
        getAction(editor, CopyAction.ACTION_ID));
      actionBars.setGlobalActionHandler(
        ActionFactory.PASTE.getId(),
        getAction(editor, PasteAction.ACTION_ID));
      actionBars.setGlobalActionHandler(
        ActionFactory.SELECT_ALL.getId(),
        getAction(editor, GEFActionConstants.SELECT_ALL));
      actionBars.setGlobalActionHandler(
        IDEActionFactory.BOOKMARK.getId(),
        getAction(editor, IDEActionFactory.BOOKMARK.getId()));
      actionBars.updateActionBars();
    }
  }

}
