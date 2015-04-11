/**
 * 
 */
package org.activiti.designer.features;

import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * @author Tijs Rademakers
 * @since 5.17
 * 
 */
public class CreateCustomUserTaskFeature extends CreateUserTaskFeature {

	public CreateCustomUserTaskFeature(IFeatureProvider fp, String name, String description,
			String customServiceTaskId) {
		super(fp, name, description, customServiceTaskId);
	}

}
