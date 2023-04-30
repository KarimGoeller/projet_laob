package application.cells;

import measures.MeasureType;

/**
 * Controller for a {@link MeasuresCell}
 * @author davidroussel
 */
public class MeasuresCellController extends AbstractCustomCellController<MeasureType>
{
	/**
	 * Set image in {@link AbstractCustomCellController#iconView} depending on
	 * the {@link MeasureType}
	 * @param value the measure type to decide which {@link javafx.scene.image.Image}
	 * to set in {@link AbstractCustomCellController#iconView}
	 */
	@Override
	public void setIcon(MeasureType value)
	{
		setImage(MeasuresIconsFactory.getIconFromType(value));
	}
}
