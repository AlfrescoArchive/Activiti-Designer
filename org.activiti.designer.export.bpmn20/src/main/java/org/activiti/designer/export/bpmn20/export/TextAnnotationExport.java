package org.activiti.designer.export.bpmn20.export;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.TextAnnotation;
import org.apache.commons.lang.StringUtils;


public class TextAnnotationExport implements ActivitiNamespaceConstants {
  
  public static void createTextAnnotation(final Object object, final XMLStreamWriter xtw) throws Exception {
    final TextAnnotation ta = (TextAnnotation) object;
    
    // start text annotation element
    xtw.writeStartElement("textAnnotation");
    xtw.writeAttribute("id", ta.getId());
    
    // TODO: in case we support others than this, add a new attribute for it
    if (StringUtils.isNotEmpty(ta.getTextFormat()))
    {
      xtw.writeAttribute("textFormat", ta.getTextFormat());
    } else {
      xtw.writeAttribute("textFormat", "text/plain");
    }
    
    // start inner text element
    xtw.writeStartElement("text");
    // write the text as CDATA
    xtw.writeCData(ta.getText());
    // end the inner text element
    xtw.writeEndElement();
    
    // end the annotation element
    xtw.writeEndElement();
  }
}
