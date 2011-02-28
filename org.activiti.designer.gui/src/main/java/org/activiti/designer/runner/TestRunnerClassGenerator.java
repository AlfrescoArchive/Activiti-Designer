package org.activiti.designer.runner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class TestRunnerClassGenerator {
	
	private String processId;
	private String processName;

	public void generateTestClass(IResource bpmnResource) throws Exception {
		IProject project = bpmnResource.getProject();
		IFolder sourceFolder = project.getFolder("src").getFolder("test").getFolder("java");
		IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
		IPackageFragmentRoot srcRoot = javaProject.getPackageFragmentRoot(sourceFolder);

		IPackageFragment pack = srcRoot.createPackageFragment(
				"org.activiti.designer.test", false, null);
		
		parseBpmnXML(bpmnResource.getRawLocation().toOSString());
		
		String testClassName = "ProcessTest" + processId.substring(0, 1).toUpperCase() + processId.substring(1) + ".java";
		testClassName = testClassName.replace(" ", "");
		testClassName = testClassName.replace("_", "");
		testClassName = testClassName.replace("-", "");

		ICompilationUnit cu = pack.createCompilationUnit(testClassName,
				createTestClass(bpmnResource, testClassName, pack), false, null);
		
		IFolder testResourceFolder = project.getFolder("src").getFolder("test").getFolder("resources");
    IFile propertiesFile = testResourceFolder.getFile("activiti.cfg.xml");
    InputStream source = new ByteArrayInputStream(createConfigFile().getBytes()); 
    propertiesFile.create(source, true, null);
    source.close();
	}
	
	private String createTestClass(IResource bpmnResource, String className, IPackageFragment pack) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n\n")
		    .append("import static org.junit.Assert.*;\n\n")
		    .append("import java.util.HashMap;\n")
		    .append("import java.util.Map;\n\n")
		    .append("import org.activiti.engine.RuntimeService;\n")
		    .append("import org.activiti.engine.runtime.ProcessInstance;\n")
		    .append("import org.activiti.engine.test.Deployment;\n")
		    .append("import org.activiti.engine.test.ActivitiRule;\n")
		    .append("import org.junit.Rule;\n")
		    .append("import org.junit.Test;\n\n")
		    .append("public class ")
		    .append(className.substring(0, className.length() - 5))
		    .append(" {\n\n")
		    .append("@Rule\n") 
        .append("public ActivitiRule activitiRule = new ActivitiRule();\n\n")
		    .append("\t@Test\n")
		    .append("\t@Deployment(resources=\"diagrams/" + bpmnResource.getName() + "\")\n")
		    .append("\tpublic void startProcess() {\n")
		    .append("\t\tRuntimeService runtimeService = activitiRule.getRuntimeService();\n")
		    .append("\t\tMap<String, Object> variableMap = new HashMap<String, Object>();\n")
		    .append("\t\tvariableMap.put(\"name\", \"Activiti\");\n")
		    .append("\t\tProcessInstance processInstance = runtimeService.startProcessInstanceByKey(\"" + processId + "\", variableMap);\n")
		    .append("\t\tassertNotNull(processInstance.getId());\n")
		    .append("\t\tSystem.out.println(\"id \" + processInstance.getId() + \" \"\n")
		    .append("\t\t\t\t+ processInstance.getProcessDefinitionId());\n")
		    .append("\t}\n")
		    .append("}");
		return buffer.toString();
	}
	
	private String createConfigFile() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    buffer.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"\n");
    buffer.append("\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
    buffer.append("\t\txsi:schemaLocation=\"http://www.springframework.org/schema/beans\n");
    buffer.append("\t\t\t\thttp://www.springframework.org/schema/beans/spring-beans.xsd\">\n\n");
    buffer.append("\t<bean id=\"processEngineConfiguration\" class=\"org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration\">\n");
    buffer.append("\t\t<property name=\"databaseSchemaUpdate\" value=\"true\"/>\n");
    buffer.append("\t</bean>\n");
    buffer.append("</beans>");
    return buffer.toString();
  }
	
	private void parseBpmnXML(String filePath) {
		try {
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			IProject[] ps = ws.getRoot().getProjects();
			String strLocation = null;
			if(ps == null || ps.length == 0) return;
			
			IProject p = ps[0];
			IPath location = p.getLocation();
			strLocation = location.toFile().getAbsolutePath();
			strLocation = strLocation.substring(0, strLocation.lastIndexOf(File.separator));
			XMLInputFactory xif = XMLInputFactory.newInstance();
			InputStreamReader in = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
			XMLStreamReader xtr = xif.createXMLStreamReader(in);
			while(xtr.hasNext()) {
				xtr.next();
				if(xtr.isStartElement() && "process".equalsIgnoreCase(xtr.getLocalName())) {
					processId = xtr.getAttributeValue(null, "id");
					processName = xtr.getAttributeValue(null, "name");
				}
			}
			xtr.close();
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
