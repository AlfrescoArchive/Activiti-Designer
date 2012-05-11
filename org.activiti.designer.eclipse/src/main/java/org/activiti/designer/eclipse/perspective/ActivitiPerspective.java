package org.activiti.designer.eclipse.perspective;

import org.activiti.designer.eclipse.views.navigator.ActivitiNavigator;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ActivitiPerspective implements IPerspectiveFactory {

  public void createInitialLayout(IPageLayout layout) {
    defineActions(layout);
    defineLayout(layout);
  }

  /**
   * Defines the initial actions for a page.
   * 
   * @param layout
   *          The layout we are filling
   */
  private void defineActions(IPageLayout layout) {
    // Add "new wizards".
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$

    // Add "show views".
    layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
    layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
    layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);

    layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
  }

  /**
   * Defines the initial layout for a page.
   * 
   * @param layout
   *          The layout we are filling
   */
  private void defineLayout(IPageLayout layout) {
    // Editors are placed for free.
    String editorArea = layout.getEditorArea();

    // Top left.
    IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float) 0.26, editorArea);//$NON-NLS-1$
    topLeft.addView(ActivitiNavigator.VIEW_ID);
    topLeft.addView("org.eclipse.jdt.ui.PackageExplorer");
    topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
    topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

    // Bottom left.
    IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float) 0.50,//$NON-NLS-1$
            "topLeft");//$NON-NLS-1$
    bottomLeft.addView(IPageLayout.ID_OUTLINE);
    bottomLeft.addView("org.eclipse.graphiti.ui.internal.editor.thumbnailview");

    // Bottom right.
    IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, (float) 0.66,//$NON-NLS-1$
            editorArea);
    bottomRight.addView(IPageLayout.ID_PROP_SHEET);
    bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
    bottomRight.addView("org.eclipse.ant.ui.views.AntView");
    bottomRight.addView("org.eclipse.pde.runtime.LogView");
  }
}
