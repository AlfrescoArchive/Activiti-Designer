package org.acme.palette;

import java.util.ArrayList;
import java.util.List;

import org.activiti.designer.integration.palette.AbstractDefaultPaletteCustomizer;
import org.activiti.designer.integration.palette.PaletteEntry;

/**
 * Customizes the palette for the Money Tasks.
 * 
 * @author John Doe
 * @since 1.0.0
 * @version 1
 */
public class AcmeMoneyCustomizer extends AbstractDefaultPaletteCustomizer {

	public List<PaletteEntry> disablePaletteEntries() {
		List<PaletteEntry> result = new ArrayList<PaletteEntry>();
		//Disable the mail task
		result.add(PaletteEntry.MAIL_TASK);
		return result;
	}

}
