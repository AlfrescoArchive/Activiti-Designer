/**
 * 
 */
package org.activiti.designer.features;

import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public class CreateCustomServiceTaskFeature extends CreateServiceTaskFeature {

	public CreateCustomServiceTaskFeature(IFeatureProvider fp, String name, String description,
			String customServiceTaskId) {
		super(fp, name, description, customServiceTaskId);
	}

}
