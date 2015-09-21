package org.activiti.designer.features;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.designer.eclipse.common.ActivitiPlugin;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditor;
import org.activiti.designer.eclipse.editor.ActivitiDiagramEditorInput;
import org.activiti.designer.util.eclipse.ActivitiUiUtil;
import org.activiti.designer.util.preferences.Preferences;
import org.activiti.designer.util.preferences.PreferencesUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * @author Tiese Barrell
 * @version 2
 * @since 0.5.0
 * 
 */
public abstract class AbstractCreateBPMNConnectionFeature extends AbstractCreateConnectionFeature {

  public AbstractCreateBPMNConnectionFeature(IFeatureProvider fp, String name, String description) {
    super(fp, name, description);
  }

  protected abstract String getFeatureIdKey();

  protected abstract Class<? extends BaseElement> getFeatureClass();

  protected String getNextId() {
    if (!PreferencesUtil.getBooleanPreference(Preferences.EDITOR_ENABLE_MULTI_DIAGRAM, ActivitiPlugin.getDefault())) {
      return ActivitiUiUtil.getNextId(getFeatureClass(), getFeatureIdKey(), getDiagram());
    } else {
      return ActivitiUiUtil.getNextId(getFeatureClass(), getFeatureIdKey(), getTopLevelDiagram());
    }
  }

  private Diagram getTopLevelDiagram()
  {
    ActivitiDiagramEditor editor = (ActivitiDiagramEditor)getDiagramBehavior().getDiagramContainer();
    ActivitiDiagramEditorInput adei=(ActivitiDiagramEditorInput)editor.getDiagramEditorInput();
    ActivitiDiagramEditor parent= adei.getParentEditor();
    while (parent!=null)
    {
      editor=parent;
      adei=(ActivitiDiagramEditorInput)parent.getDiagramEditorInput();
      parent =adei.getParentEditor();
    }
    return editor.getDiagramTypeProvider().getDiagram();
  }

}
