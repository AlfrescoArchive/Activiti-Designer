/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.designer.eclipse.property;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.ui.views.properties.tabbed.ITypeMapper;

public class TypeMapper implements ITypeMapper {

	@Override
	public Class<? extends Object> mapType(Object object) {

		Class<? extends Object> type = object.getClass();
		if (object instanceof EditPart) {

			Object model = ((EditPart) object).getModel();

			type = model.getClass();
			if (model instanceof PictogramElement) {
				PictogramElement pe = (PictogramElement) model;

				EObject businessObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if (businessObject == null) {
					return pe.getClass();
				} else {
					return businessObject.eClass().getClass();
				}
			}

		}
		return type;
	}
}