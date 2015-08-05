package org.activiti.designer.property;

import java.util.Collection;
import java.util.Iterator;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.Message;
import org.activiti.bpmn.model.MessageEventDefinition;
import org.activiti.designer.util.editor.BpmnMemoryModel;
import org.activiti.designer.util.editor.ModelHandler;
import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

public class MessagePropertyUtil {
	
  public static String[] fillMessageCombo(Combo messageCombo, SelectionListener selectionListener, Diagram diagram) {
    messageCombo.removeSelectionListener(selectionListener);
    BpmnMemoryModel memoryModel = ModelHandler.getModel(EcoreUtil.getURI(diagram));
    BpmnModel model = memoryModel.getBpmnModel();
    String[] messageArray = new String[model.getMessages().size()];
    Collection<Message> messages = model.getMessages();
    int counter = 0;
    Iterator<Message> itMessage = messages.iterator();
    while (itMessage.hasNext()) {
      Message message = itMessage.next();
      messageArray[counter] = message.getName() + " (" + message.getId() + ")";
      counter++;
    }
    messageCombo.setItems(messageArray);
    messageCombo.select(0);
    messageCombo.addSelectionListener(selectionListener);
    return messageArray;
  }


  public static String getMessageValue(Event event, Diagram diagram) {
    if (event.getEventDefinitions().get(0) != null) {
      MessageEventDefinition messageDefinition = (MessageEventDefinition) event.getEventDefinitions().get(0);
      if (StringUtils.isNotEmpty(messageDefinition.getMessageRef())) {
        BpmnMemoryModel memoryModel = ModelHandler.getModel(EcoreUtil.getURI(diagram));
        BpmnModel model = memoryModel.getBpmnModel();
        for (Message message : model.getMessages()) {
          if (message.getId() != null && message.getId().equals(messageDefinition.getMessageRef())) {
            return message.getName() + " (" + message.getId() + ")";
          }
        }
        
      } else {
        return "message";
      }
    }
    return null;
  }

  public static void storeMessageValue(Combo messageCombo, Event event, Diagram diagram) {
    MessageEventDefinition messageDefinition = (MessageEventDefinition) event.getEventDefinitions().get(0);
    String messageKey = messageCombo.getItem(messageCombo.getSelectionIndex());
    messageDefinition.setMessageRef(messageKey.substring(messageKey.lastIndexOf("(") + 1, messageKey.length() - 1));
  }
}
