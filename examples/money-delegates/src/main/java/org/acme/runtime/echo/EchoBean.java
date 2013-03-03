package org.acme.runtime.echo;

import org.activiti.engine.delegate.DelegateExecution;

public class EchoBean {

  private static final String PREFIX = "Customer name";

  private static final String ECHO_FORMAT = "%s: %s";

  public void echo(DelegateExecution execution) throws Exception {
    System.out.println(String.format(ECHO_FORMAT, PREFIX, execution.getVariable("customerName")));
  }

}
