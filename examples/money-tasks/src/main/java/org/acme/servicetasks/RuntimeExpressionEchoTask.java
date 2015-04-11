/**
 * 
 */
package org.acme.servicetasks;

import org.activiti.designer.integration.annotation.Help;
import org.activiti.designer.integration.annotation.Runtime;
import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;

/**
 * Example of a CustomServiceTask that uses an expression for the Runtime
 * annotation.
 */
@Runtime(expression = "${echoBean.echo(execution)}")
@Help(displayHelpShort = "Echos the customerName", displayHelpLong = "Echoes the process variable customerName")
public class RuntimeExpressionEchoTask extends AbstractCustomServiceTask {

  @Override
  public String contributeToPaletteDrawer() {
    return "Acme Corporation";
  }

  @Override
  public String getName() {
    return "Echo customer name (expression)";
  }

  @Override
  public String getSmallIconPath() {
    return "icons/coins.png";
  }

}
