package application.cells;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import logger.LoggerFactory;
import measures.MeasureType;
import utils.IconFactory;

/**
 * Icon Sub-Factory dedicated to Measures types icons.
 * Suitable for {@link MeasuresCellController}
 * @author davidroussel
 */
public class MeasuresIconsFactory
{
	/**
	 * Icon for "length" measures
	 */
	private static Image lengthIcon = IconFactory.getIcon("length");

	/**
	 * Icon for "area" measures
	 */
	private static Image areaIcon = IconFactory.getIcon("surface");

	/**
	 * Icon for "volume" measures
	 */
	private static Image volumeIcon = IconFactory.getIcon("volume");

	/**
	 * Icon for "weight" measures
	 */
	private static Image weightIcon = IconFactory.getIcon("weight");

	/**
	 * Icon for "speed" measures
	 */
	private static Image speedIcon = IconFactory.getIcon("speed");

	/**
	 * Icon for "pressure" measures
	 */
	private static Image pressureIcon = IconFactory.getIcon("pressure");

	/**
	 * Icon for "direction" measures
	 */
	private static Image directionIcon = IconFactory.getIcon("compass");

	/**
	 * Icon for "temperature" measures
	 */
	private static Image temperatureIcon = IconFactory.getIcon("temperature");

	/**
	 * Icon for "time" measures
	 */
	private static Image timeIcon = IconFactory.getIcon("time");

	/**
	 * Logger to use
	 */
	private static Logger logger = LoggerFactory.getParentLogger(MeasuresIconsFactory.class,
	                                                             IconFactory.getLogger(),
	                                                             (IconFactory.getLogger() == null ?
	                                                              Level.INFO : null)); // null level to inherits parent logger's level

	/**
	 * Retriev icon image from {@link MeasureType}s
	 * @param type the type of measure to icon
	 * @return the icon corresponding to the provided measure type
	 */
	public static Image getIconFromType(MeasureType type)
	{
		switch(type)
		{
			case LENGTH :
				return lengthIcon;
			case AREA :
				return areaIcon;
			case VOLUME :
				return volumeIcon;
			case WEIGHT :
				return weightIcon;
			case SPEED :
				return speedIcon;
			case PRESSURE :
				return pressureIcon;
			case DIRECTION :
				return directionIcon;
			case TEMPERATURE :
				return temperatureIcon;
			case TIME :
				return timeIcon;
			default:
				String message = "Unexpected value: " + type.toString();
				logger.severe(message);
				throw new IllegalArgumentException(message);
		}
	}
}
