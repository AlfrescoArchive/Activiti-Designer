package org.activiti.designer.export.bpmn20.export;

import javax.xml.stream.XMLStreamWriter;

import org.activiti.designer.bpmn2.model.Association;


public class AssociationExport implements ActivitiNamespaceConstants {

  public static void createAssociation(Object object, XMLStreamWriter xtw) throws Exception {
    Association association = (Association) object;
    
    // start element
    xtw.writeStartElement("association");
    xtw.writeAttribute("id", association.getId());
    xtw.writeAttribute("sourceRef", association.getSourceRef().getId());
    xtw.writeAttribute("targetRef", association.getTargetRef().getId());
    
    if (association.getAssociationDirection() != null) {
      xtw.writeAttribute("associationDirection", association.getAssociationDirection().getValue());
    }
    
    xtw.writeEndElement();
  } 
}
