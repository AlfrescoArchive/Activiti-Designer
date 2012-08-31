package org.activiti.designer.eclipse.extension.palette;

import java.util.List;

import org.activiti.designer.integration.servicetask.CustomServiceTaskDescriptor;

public interface IPaletteProvider {
	List<CustomServiceTaskDescriptor> provide();
}
