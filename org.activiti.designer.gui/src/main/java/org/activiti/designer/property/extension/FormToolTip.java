package org.activiti.designer.property.extension;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledFormText;

public class FormToolTip extends ToolTip {

	private String title;
	private String[] texts;

	public FormToolTip(final Control control, final String title, final String... texts) {
		super(control, ToolTip.NO_RECREATE, true);
		this.title = title;
		this.texts = texts;
	}

	protected Composite createToolTipContentArea(Event event, Composite parent) {

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		FormColors colors = toolkit.getColors();
		Color top = colors.getColor(IFormColors.H_GRADIENT_END);
		Color bot = colors.getColor(IFormColors.H_GRADIENT_START);

		// create the base form
		Form form = toolkit.createForm(parent);
		form.setText(title);
		form.setTextBackground(new Color[] { top, bot }, new int[] { 100 }, true);
		FormLayout layout = new FormLayout();
		layout.marginTop = 10;
		layout.marginBottom = 10;
		layout.marginLeft = 10;
		layout.marginRight = 10;
		form.getBody().setLayout(layout);

		// Scrolled text
		ScrolledFormText scrolledFormText = new ScrolledFormText(form.getBody(), true);
		FormText text = toolkit.createFormText(scrolledFormText, true);

		scrolledFormText.setAlwaysShowScrollBars(false);

		StringBuilder builder = new StringBuilder();
		for (final String currentText : texts) {
			builder.append("<p>").append(currentText).append("</p>");
		}

		text.setText(String.format("<form>%s</form>", builder.toString()), true, false);

		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100);
		scrolledFormText.setLayoutData(data);

		scrolledFormText.setFormText(text);
		scrolledFormText.setBackground(ColorConstants.white);

		return parent;
	}
}
