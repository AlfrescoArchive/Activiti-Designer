/**
 * 
 */
package org.activiti.designer.integration.palette;

import java.util.List;

/**
 * Customizes entries in the default Palette.
 * 
 * @author Tiese Barrell
 * @since 0.5.1
 * @version 1
 * 
 */
public interface DefaultPaletteCustomizer {

	public List<PaletteEntry> disablePaletteEntries();

}
