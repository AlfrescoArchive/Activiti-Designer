/**
 * 
 */
package org.acme.servicetasks;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Example of a CustomServiceTask that uses an expression for the Runtime
 * annotation.
 */
@Runtime(expression = "${echoBean.echo(execution)}")
@Help(displayHelpShort = "Echos the customerName", displayHelpLong = "Echoes the process variable customerName")
public class RuntimeExpressionEchoTask extends AbstractCustomServiceTask {

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
    return "Echo customer name (expression)";
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
