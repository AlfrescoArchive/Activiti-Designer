package org.activiti.designer.kickstart.form.features;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.activiti.designer.kickstart.form.diagram.KickstartFormFeatureProvider;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.algorithms.Text;

/**
 * @author Frederik Heremans
 */
public class UpdateDateFormPropertyFeature extends UpdateFormPropertyFeature {

  public UpdateDateFormPropertyFeature(KickstartFormFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    return getBusinessObjectForPictogramElement(context.getPictogramElement()) instanceof DatePropertyDefinition;
  }

  @Override
  public boolean update(IUpdateContext context) {
    boolean success = super.update(context);
    if(success) {
      DatePropertyDefinition propDef = (DatePropertyDefinition) getBusinessObjectForPictogramElement(context.getPictogramElement());
      String dateValueString = null;
      if(propDef.isShowTime()) {
        Calendar noon = Calendar.getInstance();
        noon.set(Calendar.HOUR_OF_DAY, 12);
        noon.set(Calendar.MINUTE, 0);
        noon.set(Calendar.SECOND, 0);
        dateValueString = DateFormat.getDateTimeInstance().format(noon.getTime());
      } else {
        dateValueString = DateFormat.getDateInstance().format(new Date());
      }
      
      Text fieldText = findFieldText(context.getPictogramElement());
      if(fieldText != null && hasChanged(fieldText.getValue(), dateValueString)) {
        fieldText.setValue(dateValueString);
      }
    }
    return success;
  }
}
