/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package org.activiti.designer.eclipse.common;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonMenuConstants;

public class RenameActionProvider extends CommonActionProvider {

	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		ISelection selection = getContext().getSelection();
		if (selection.isEmpty())
			return;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object el = sel.getFirstElement();
			if (el instanceof EClass) {
				EClass eclass = (EClass) el;
				String platformString = eclass.eResource().getURI().toPlatformString(true);
				Path path = new Path(platformString);
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (file == null)
					return;
				IProject project = file.getProject();
				try {
					if (!project.hasNature(ActivitiProjectNature.NATURE_ID))
						return;
				} catch (CoreException e) {
					e.printStackTrace();
				}
				menu.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, getAction(eclass));

			}
		}
	}

	private IAction getAction(EClass eo) {
		return new RenameAction(eo);
	}

	private static class RenameAction extends Action {

		private EClass eclass;

		public RenameAction(EClass eo) {
			this.eclass = eo;
		}

		@Override
		public void run() {
			InputDialog inputDialog = new InputDialog(null, "Provide String", "Provide a new Name for the EClass", eclass.getName(), null);
			int open = inputDialog.open();
			if (open == Dialog.OK) {
				String newName = inputDialog.getValue();
				Resource resource = eclass.eResource();
				ResourceSet resourceSet = resource.getResourceSet();
				TransactionalEditingDomain domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
				try{
				if (domain != null){
					Command setCommand = domain.createCommand(SetCommand.class, new CommandParameter(eclass,
							EcorePackage.Literals.ENAMED_ELEMENT__NAME, newName));
					domain.getCommandStack().execute(setCommand);
					try {
						resource.save(Collections.emptyMap());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				}finally{
					domain.dispose();
				}
			}
		}

		@Override
		public String getText() {
			return "Rename EClass";
		}
	}

}
