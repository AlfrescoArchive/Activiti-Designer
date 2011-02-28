/**
 * 
 */
package org.acme.servicetasks;

import java.util.List;

import org.activiti.designer.integration.servicetask.AbstractCustomServiceTask;
import org.activiti.designer.integration.servicetask.PropertyType;
import org.activiti.designer.integration.servicetask.annotation.DataGridProperty;
import org.activiti.designer.integration.servicetask.annotation.DatePickerProperty;
import org.activiti.designer.integration.servicetask.annotation.Help;
import org.activiti.designer.integration.servicetask.annotation.Property;
import org.activiti.designer.integration.servicetask.annotation.PropertyItems;
import org.activiti.designer.integration.servicetask.annotation.Runtime;

/**
 * Defines the Money Task node.
 * 
 * @author John Doe
 * @version 1
 * @since 1.0.0
 */
@Runtime(delegationClass = "org.acme.runtime.AcmeMoneyJavaDelegation")
@Help(displayHelpShort = "Creates a new account", displayHelpLong = "Creates a new account using the account number specified")
public class AcmeMoneyTask extends AbstractCustomServiceTask {

  private static final String HELP_ACCOUNT_NUMBER_LONG = "Provide a number that is suitable as an account number.";

  private static final String ACCOUNT_TYPE_SAVINGS_LABEL = "Savings account";
  private static final String ACCOUNT_TYPE_SAVINGS_VALUE = "savings";

  private static final String ACCOUNT_TYPE_JUNIOR_LABEL = "Piggy Bank Account";
  private static final String ACCOUNT_TYPE_JUNIOR_VALUE = "junior";

  private static final String ACCOUNT_TYPE_JOINT_LABEL = "Joint account";
  private static final String ACCOUNT_TYPE_JOINT_VALUE = "joint";

  private static final String ACCOUNT_TYPE_TRANSACTIONAL_LABEL = "Transactional account";
  private static final String ACCOUNT_TYPE_TRANSACTIONAL_VALUE = "transactional";

  private static final String ACCOUNT_TYPE_STUDENT_LABEL = "Student account";
  private static final String ACCOUNT_TYPE_STUDENT_VALUE = "student";

  private static final String ACCOUNT_TYPE_SENIOR_LABEL = "Senior Citizen Account";
  private static final String ACCOUNT_TYPE_SENIOR_VALUE = "senior";

  private static final String LIMIT_LOW_LABEL = "Low (250)";
  private static final String LIMIT_LOW_VALUE = "250";
  private static final String LIMIT_MEDIUM_LABEL = "Medium (1000)";
  private static final String LIMIT_MEDIUM_VALUE = "1000";
  private static final String LIMIT_HIGH_LABEL = "High (2500)";
  private static final String LIMIT_HIGH_VALUE = "2500";

  @Property(type = PropertyType.TEXT, displayName = "Account Number", required = true)
  @Help(displayHelpShort = "Provide an account number", displayHelpLong = HELP_ACCOUNT_NUMBER_LONG)
  private String accountNumber;

  @Property(type = PropertyType.MULTILINE_TEXT, displayName = "Comments", required = true)
  @Help(displayHelpShort = "Provide comments", displayHelpLong = "You can add comments to the node to provide a brief description.")
  private String comments;

  @Property(type = PropertyType.PERIOD, displayName = "Processing Time", required = true)
  @Help(displayHelpShort = "The maximum allowed time for processing", displayHelpLong = "Processing must take no longer than the period you specify here.")
  private String maximumProcessingTime;

  @Property(type = PropertyType.DATA_GRID, displayName = "Account managers")
  @DataGridProperty(itemClass = AccountManager.class, orderable = true)
  @Help(displayHelpShort = "People authorized for this account", displayHelpLong = "Specify the details of people allowed to manage this account.")
  private List<AccountManager> accountManagers;

  @Property(type = PropertyType.BOOLEAN_CHOICE, displayName = "VIP Customer")
  @Help(displayHelpShort = "Is the customer a VIP?", displayHelpLong = "VIP customers enjoy special privileges. Check this field to indicate the customer is a VIP.")
  private String vipCustomer;

  @Property(type = PropertyType.COMBOBOX_CHOICE, displayName = "Account type", required = true)
  @Help(displayHelpShort = "The type of account", displayHelpLong = "Choose a type of account from the list of options")
  @PropertyItems({ ACCOUNT_TYPE_SAVINGS_LABEL, ACCOUNT_TYPE_SAVINGS_VALUE, ACCOUNT_TYPE_JUNIOR_LABEL, ACCOUNT_TYPE_JUNIOR_VALUE, ACCOUNT_TYPE_JOINT_LABEL,
      ACCOUNT_TYPE_JOINT_VALUE, ACCOUNT_TYPE_TRANSACTIONAL_LABEL, ACCOUNT_TYPE_TRANSACTIONAL_VALUE, ACCOUNT_TYPE_STUDENT_LABEL, ACCOUNT_TYPE_STUDENT_VALUE,
      ACCOUNT_TYPE_SENIOR_LABEL, ACCOUNT_TYPE_SENIOR_VALUE })
  private String accountType;

  @Property(type = PropertyType.RADIO_CHOICE, displayName = "Withdrawl limit", required = true)
  @Help(displayHelpShort = "The maximum daily withdrawl amount ", displayHelpLong = "Choose the maximum daily amount that can be withdrawn from the account.")
  @PropertyItems({ LIMIT_LOW_LABEL, LIMIT_LOW_VALUE, LIMIT_MEDIUM_LABEL, LIMIT_MEDIUM_VALUE, LIMIT_HIGH_LABEL, LIMIT_HIGH_VALUE })
  private String withdrawlLimit;

  @Property(type = PropertyType.DATE_PICKER, displayName = "Expiry date", required = true)
  @Help(displayHelpShort = "The date the account expires ", displayHelpLong = "Choose the date when the account will expire if no extended before the date.")
  @DatePickerProperty()
  private String expiryDate;

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
    return "Money node";
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
