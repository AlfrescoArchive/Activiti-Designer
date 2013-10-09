package org.activiti.designer.kickstart.eclipse.navigator;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.FolderType;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BindingType;


public class CmisUtil {

  public static Session createCmisSession(String user, String password, String url) {
    SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
    Map<String, String> parameter = new HashMap<String, String>();
    parameter.put(SessionParameter.USER, user);
    parameter.put(SessionParameter.PASSWORD, password);
    parameter.put(SessionParameter.ATOMPUB_URL, url); 
    parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

    Repository repository = sessionFactory.getRepositories(parameter).get(0);
    return repository.createSession();
  }
  
  public static List<CmisObject> getRootElements(Session session) {
	  Folder rootFolder = session.getRootFolder();
	  ItemIterable<CmisObject> children = rootFolder.getChildren();
	  List<CmisObject> cmisObjects = new ArrayList<CmisObject>();
	  for (CmisObject cmisObject : children) {
		  cmisObjects.add(cmisObject);
	  }
	  return cmisObjects;
  }
  
  public static Folder getFolder(Session session, String folderName) {
    ObjectType type = session.getTypeDefinition("cmis:folder");
    PropertyDefinition<?> objectIdPropDef = type.getPropertyDefinitions().get(PropertyIds.OBJECT_ID);
    String objectIdQueryName = objectIdPropDef.getQueryName();
    
    ItemIterable<QueryResult> results = session.query("SELECT * FROM cmis:folder WHERE cmis:name='" + folderName + "'", false);
    for (QueryResult qResult : results) {
    	String objectId = qResult.getPropertyValueByQueryName(objectIdQueryName);
    	return (Folder) session.getObject(session.createObjectId(objectId));
    }
    return null;
  }
  
  public static Folder createFolder(Session session, Folder parentFolder, String folderName) {
    Map<String, Object> folderProps = new HashMap<String, Object>();
    folderProps.put(PropertyIds.NAME, folderName);
    folderProps.put(PropertyIds.OBJECT_TYPE_ID, FolderType.FOLDER_BASETYPE_ID);

    ObjectId folderObjectId = session.createFolder(folderProps, parentFolder, null, null, null);
    return (Folder) session.getObject(folderObjectId);
  }
  
  public static Document createDocument(Session session, Folder folder, String fileName, String mimetype, byte[] content) throws Exception {
    Map<String, Object> docProps = new HashMap<String, Object>();
    docProps.put(PropertyIds.NAME, fileName);
    docProps.put(PropertyIds.OBJECT_TYPE_ID, DocumentType.DOCUMENT_BASETYPE_ID);
    
    ByteArrayInputStream in = new ByteArrayInputStream(content);
    ContentStream contentStream = session.getObjectFactory().createContentStream(fileName, content.length, mimetype, in);
    
    ObjectId documentId = session.createDocument(docProps, session.createObjectId((String) folder.getPropertyValue(PropertyIds.OBJECT_ID)), contentStream, null, null, null, null);
    Document document = (Document) session.getObject(documentId);
    return document;
  }
  
}
