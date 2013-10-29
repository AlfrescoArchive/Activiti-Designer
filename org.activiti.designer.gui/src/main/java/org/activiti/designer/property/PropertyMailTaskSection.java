package org.activiti.designer.property;

import org.activiti.bpmn.model.ServiceTask;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PropertyMailTaskSection extends ActivitiPropertySection implements ITabbedPropertyConstants {

	private Text toText;
	private Text fromText;
	private Text subjectText;
	private Text ccText;
	private Text bccText;
	private Text charsetText;
	private Text htmlText;
	private Text nonHtmlText;
	
	@Override
  public void createFormControls(TabbedPropertySheetPage aTabbedPropertySheetPage) {
	  toText = createTextControl(false);
    createLabel("To", toText);
    fromText = createTextControl(false);
    createLabel("From", fromText);
    subjectText = createTextControl(false);
    createLabel("Subject", subjectText);
    ccText = createTextControl(false);
    createLabel("Cc", ccText);
    bccText = createTextControl(false);
    createLabel("Bcc", bccText);
    charsetText = createTextControl(false);
    createLabel("Charset", charsetText);
    htmlText = createTextControl(true);
    createLabel("Html", htmlText);
    nonHtmlText = createTextControl(true);
    createLabel("Non html", nonHtmlText);
  }

  @Override
  protected Object getModelValueForControl(Control control, Object businessObject) {
    ServiceTask task = (ServiceTask) businessObject;
    if (control == toText) {
      return getFieldString("to", task);
    } else if (control == fromText) {
      return getFieldString("from", task);
    } else if (control == subjectText) {
      return getFieldString("subject", task);
    } else if (control == ccText) {
      return getFieldString("cc", task);
    } else if (control == bccText) {
      return getFieldString("bcc", task);
    } else if (control == charsetText) {
      return getFieldString("charset", task);
    } else if (control == htmlText) {
      return getFieldString("html", task);
    } else if (control == nonHtmlText) {
      return getFieldString("text", task);
    }
    return null;
  }

  @Override
  protected void storeValueInModel(Control control, Object businessObject) {
    ServiceTask task = (ServiceTask) businessObject;
    if (control == toText) {
      setFieldString("to", toText.getText(), task);
    } else if (control == fromText) {
      setFieldString("from", fromText.getText(), task);
    } else if (control == subjectText) {
      setFieldString("subject", subjectText.getText(), task);
    } else if (control == ccText) {
      setFieldString("cc", ccText.getText(), task);
    } else if (control == bccText) {
      setFieldString("bcc", bccText.getText(), task);
    } else if (control == charsetText) {
      setFieldString("charset", charsetText.getText(), task);
    } else if (control == htmlText) {
      setFieldString("html", htmlText.getText(), task);
    } else if (control == nonHtmlText) {
      setFieldString("text", nonHtmlText.getText(), task);
    }
  }
}
