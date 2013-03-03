package org.acme.runtime.echo;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

public class EchoJavaDelegate implements JavaDelegate {

  private static final String ECHO_FORMAT = "%s: %s";

  private Expression echoPrefix;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    System.out.println(String.format(ECHO_FORMAT, echoPrefix.getValue(execution), execution.getVariable("customerName")));
  }

  public void setEchoPrefix(Expression echoPrefix) {
    this.echoPrefix = echoPrefix;
  }

}
