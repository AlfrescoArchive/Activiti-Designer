/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.designer.export.bpmn20.export;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.MailTask;
import org.activiti.designer.bpmn2.model.alfresco.AlfrescoMailTask;
import org.apache.commons.lang.StringUtils;


/**
 * @author Tijs Rademakers
 */
public class MailTaskExport implements ActivitiNamespaceConstants {

  public static void createMailTask(Object object, XMLStreamWriter xtw) throws Exception {
  	if(object instanceof AlfrescoMailTask) {
  		writeAlfrescoScriptMailTask((AlfrescoMailTask) object, xtw);
  	} else {
  		writeServiceMailTask((MailTask) object, xtw);
  	}
  }
  
  private static void writeAlfrescoScriptMailTask(AlfrescoMailTask mailTask, XMLStreamWriter xtw) throws Exception {
  	
  	// start AlfrescoMailTask element
  	xtw.writeStartElement("serviceTask");
    xtw.writeAttribute("id", mailTask.getId());
    if (mailTask.getName() != null) {
      xtw.writeAttribute("name", mailTask.getName());
    }
    xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE,
            "class", "org.alfresco.repo.workflow.activiti.script.AlfrescoScriptDelegate");
    DefaultFlowExport.createDefaultFlow(mailTask, xtw);
    AsyncActivityExport.createAsyncAttribute(mailTask, xtw);
    
    xtw.writeStartElement("extensionElements");
    
    ExecutionListenerExport.createExecutionListenerXML(mailTask.getExecutionListeners(), false, xtw);
    
    xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.writeAttribute("name", "script");
    xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "string", ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.writeCharacters(createMailScript(mailTask));
    xtw.writeEndElement();
    xtw.writeEndElement();
    
    xtw.writeEndElement();
    
    MultiInstanceExport.createMultiInstance(mailTask, xtw);

    // end AlfrescoMailTask element
    xtw.writeEndElement();
  }
  
  private static String createMailScript(AlfrescoMailTask mailTask) {
  	StringBuilder mailBuilder = new StringBuilder();
  	mailBuilder.append("var mail = actions.create(\"mail\");\n");
  	if(StringUtils.isNotEmpty(mailTask.getTo())) {
  		mailBuilder.append("mail.parameters.to = ")
  			.append(mailTask.getTo())
  			.append(";\n");
  	}
  	if(StringUtils.isNotEmpty(mailTask.getToMany())) {
  		mailBuilder.append("mail.parameters.to_many = ")
  			.append(mailTask.getToMany())
  			.append(";\n");
  	}
  	if(StringUtils.isNotEmpty(mailTask.getSubject())) {
  		mailBuilder.append("mail.parameters.subject = ")
  			.append(mailTask.getSubject())
  			.append(";\n");
  	}
  	if(StringUtils.isNotEmpty(mailTask.getFrom())) {
  		mailBuilder.append("mail.parameters.from = ")
  			.append(mailTask.getFrom())
  			.append(";\n");
  	}
  	if(StringUtils.isNotEmpty(mailTask.getTemplate())) {
  		mailBuilder.append("mail.parameters.template = ")
  			.append(mailTask.getTemplate())
  			.append(";\n");
  	}
  	if(StringUtils.isNotEmpty(mailTask.getTemplateModel())) {
  		mailBuilder.append("mail.parameters.template_model = ")
  			.append(mailTask.getTemplateModel())
  			.append(";\n");
  	}
  	if(StringUtils.isNotEmpty(mailTask.getText())) {
  		mailBuilder.append("mail.parameters.text = ")
  			.append(mailTask.getText())
  			.append(";\n");
  	}
  	if(StringUtils.isNotEmpty(mailTask.getHtml())) {
  		mailBuilder.append("mail.parameters.html = ")
  			.append(mailTask.getHtml())
  			.append(";\n");
  	}
  	mailBuilder.append("mail.execute(bpm_package);\n");
  	return mailBuilder.toString();
  }
   
  private static void writeServiceMailTask(MailTask mailTask, XMLStreamWriter xtw) throws Exception {
  	
    // start MailTask element
    xtw.writeStartElement("serviceTask");
    xtw.writeAttribute("id", mailTask.getId());
    if (mailTask.getName() != null) {
      xtw.writeAttribute("name", mailTask.getName());
    }
    DefaultFlowExport.createDefaultFlow(mailTask, xtw);
    AsyncActivityExport.createAsyncAttribute(mailTask, xtw);
    xtw.writeAttribute(ACTIVITI_EXTENSIONS_PREFIX, ACTIVITI_EXTENSIONS_NAMESPACE, "type", "mail");

    xtw.writeStartElement("extensionElements");
    ExecutionListenerExport.createExecutionListenerXML(mailTask.getExecutionListeners(), false, xtw);

    if (StringUtils.isNotEmpty(mailTask.getTo())) {
    	writeField("to", mailTask.getTo(), xtw);
    }
    if (StringUtils.isNotEmpty(mailTask.getFrom())) {
    	writeField("from", mailTask.getFrom(), xtw);
    }
    if (StringUtils.isNotEmpty(mailTask.getSubject())) {
    	writeField("subject", mailTask.getSubject(), xtw);
    }
    if (StringUtils.isNotEmpty(mailTask.getCc())) {
    	writeField("cc", mailTask.getCc(), xtw);
    }
    if (StringUtils.isNotEmpty(mailTask.getBcc())) {
    	writeField("bcc", mailTask.getBcc(), xtw);
    }
    if (StringUtils.isNotEmpty(mailTask.getCharset())) {
    	writeField("charset", mailTask.getCharset(), xtw);
    }
    if (StringUtils.isNotEmpty(mailTask.getHtml())) {
    	writeCDataField("html", mailTask.getHtml(), xtw);
    }
    if (StringUtils.isNotEmpty(mailTask.getText())) {
    	writeCDataField("text", mailTask.getText(), xtw);
    }
    xtw.writeEndElement();
    
    MultiInstanceExport.createMultiInstance(mailTask, xtw);

    // end MailTask element
    xtw.writeEndElement();
  }
  
  private static void writeField(String name, String expression, XMLStreamWriter xtw) throws Exception {
  	xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.writeAttribute("name", name);
    xtw.writeAttribute("expression", expression);
    xtw.writeEndElement();
  }
  
  private static void writeCDataField(String name, String text, XMLStreamWriter xtw) throws Exception {
  	xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "field", ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.writeAttribute("name", name);
    xtw.writeStartElement(ACTIVITI_EXTENSIONS_PREFIX, "expression", ACTIVITI_EXTENSIONS_NAMESPACE);
    xtw.writeCData(text);
    xtw.writeEndElement();
    xtw.writeEndElement();
  }
}
