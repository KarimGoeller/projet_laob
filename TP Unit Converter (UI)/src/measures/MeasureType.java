package measures;

import javafx.beans.property.ObjectProperty;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Enum type for all types of measures
 * @author davidroussel
 */
public enum MeasureType {
	/**
	 * Length or distance measures.
	 * SI Unit: m
	 */
	LENGTH,
	/**
	 * Area.
	 * SI Unit : m^2
	 */
	AREA,
	/**
	 * Volume.
	 * SI Unit m^3
	 */
	VOLUME,
	/**
	 * Mass / Weights
	 * SI Unit kg
	 */
	WEIGHT,
	/**
	 * Speed measures.
	 * SI Unit m/s
	 */
	SPEED,
	/**
	 * Pressure / Atmosphere
	 * SI Unit hPa (rather than Pa)
	 */
	PRESSURE,
	/**
	 * Angle / Cardinal direction
	 * SI Unit radian [0..2𝜋[
	 */
	DIRECTION,
	/**
	 * Temperature
	 * SI Unit °K
	 */
	TEMPERATURE,
	/**
	 * Time / Duration
	 * SI Unit s
	 */
	TIME;

	@Override
	public String toString() throws AssertionError
	{
		switch (this)
		{
			case LENGTH:
				return new String("Lengths");
			case AREA:
				return new String("Areas");
			case VOLUME:
				return new String("Volumes");
			case WEIGHT:
				return new String("Weights");
			case SPEED:
				return new String("Speeds");
			case PRESSURE:
				return new String("Pressures");
			case DIRECTION:
				return new String("Directions");
			case TEMPERATURE:
				return new String("Temperatures");
			case TIME:
				return new String("Times");
		}
		throw new AssertionError(getClass().getSimpleName()
		    + ".toString() unknown assertion: " + this);
	}

	/**
	 * All possible measures as a collection (in order to fill a combobox for instance)
	 * @return a collection containing all possible Measures
	 */
	public static Collection<MeasureType> all()
	{
		Collection<MeasureType> list = new ArrayList<>();
		list.add(LENGTH);
		list.add(AREA);
		list.add(VOLUME);
		list.add(WEIGHT);
		list.add(SPEED);
		list.add(PRESSURE);
		list.add(DIRECTION);
		list.add(TEMPERATURE);
		list.add(TIME);
		return list;
	}
}
