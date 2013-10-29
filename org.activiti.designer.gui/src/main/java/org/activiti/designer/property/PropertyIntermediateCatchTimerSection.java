package org.activiti.designer.property;

import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyIntermediateCatchTimerSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text timeDurationText;
	private Text timeDateText;
	private Text timeCycleText;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
    timeDurationText = createTextControl(false);
    createLabel("Time duration", timeDurationText);
    timeDateText = createTextControl(false);
    createLabel("Time date (ISO 8601)", timeDateText);
    timeCycleText = createTextControl(false);
    createLabel("Time cycle", timeCycleText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    TimerEventDefinition timerDefinition = (TimerEventDefinition) event.getEventDefinitions().get(0);
    
    if (control == timeDurationText) {
      return timerDefinition.getTimeDuration();
      
    } else if (control == timeDateText) {
      return timerDefinition.getTimeDate();
      
    } else if (control == timeCycleText) {
      return timerDefinition.getTimeCycle();
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    Event event = (Event) businessObject;
    TimerEventDefinition timerDefinition = (TimerEventDefinition) event.getEventDefinitions().get(0);
    
    if (control == timeDurationText) {
      timerDefinition.setTimeDuration(timeDurationText.getText());
    
    } else if (control == timeDateText) {
      timerDefinition.setTimeDate(timeDateText.getText());
    
    } else if (control == timeCycleText) {
      timerDefinition.setTimeCycle(timeCycleText.getText());
    }
  }
}
