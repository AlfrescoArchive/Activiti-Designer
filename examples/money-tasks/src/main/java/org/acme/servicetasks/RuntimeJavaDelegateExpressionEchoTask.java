/**
 * 
 */
package org.acme.servicetasks;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Example of a CustomServiceTask that uses a Java Delegate Expression for the
 * Runtime annotation.
 */
@Runtime(javaDelegateExpression = "${echoJavaDelegateExpressionBean}")
@Help(displayHelpShort = "Echos the customerName", displayHelpLong = "Echoes the process variable customerName")
public class RuntimeJavaDelegateExpressionEchoTask extends AbstractCustomServiceTask {

  @Property(type = PropertyType.TEXT, displayName = "Prefix", required = true, defaultValue = "Customer name")
  @Help(displayHelpShort = "Provide a prefix for the echo")
  private String echoPrefix;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.activiti.designer.integration.servicetask.AbstractCustomServiceTask
   * #contributeToPaletteDrawer()
   */
  @Override
  public String contributeToPaletteDrawer() {
    return "Acme Corporation";
  }

  @Override
  public String getName() {
    return "Echo customer name (javaDelegateExpression)";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.activiti.designer.integration.servicetask.AbstractCustomServiceTask
   * #getSmallIconPath()
   */
  @Override
  public String getSmallIconPath() {
    return "icons/coins.png";
  }

}
