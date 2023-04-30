package measures.units;

import java.sql.SQLOutput;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import measures.MeasureType;

/**
 * A base abstract class for all units.
 * A unit is composed of:
 * <ul>
 * <li>A description (which could appear in some UI)</li>
 * <li>A symbol (used to place near values expressed with this unit)</li>
 * <li>An optional value stored in this unit</li>
 * </ul>
 * A Unit shall:
 * <ul>
 * <li>Be printable (through toString) with description and/or symbol</li>
 * <li>Be comparable with equals(Object) as well as compareTo(Unit<?>)</li>
 * <li>Be hashable (through hashCode)</li>
 * <li>format a provided value into a {@link String} that can be presented in a
 * UI. e.g. "96.5 [km/h]"</li>
 * <li>format the internal optional value (if available) in the same way as the
 * previous statement</li>
 * <li>Convert value from another unit into its internal value</li>
 * </ul>
 * All Units are:
 * <ul>
 * 	<li>Comparable in order to be sorted</li>
 * </ul>
 * And therefore a Unit also contains an internal enum defining the order to
 * apply when comparing Units.
 * @author davidroussel
 * @param <E> the type of data measured with this unit
 */
public abstract class Unit<E extends Comparable<E>>
    implements Comparable<Unit<?>>, Setable<E>
{
	/**
	 * The type of measures this unit is realted to.
	 * SPEED, LENGTH, etc.
	 */
	protected final MeasureType type;

	/**
	 * Description of this unit.
	 * E.g. : "Kilometers per Hour"
	 * @implSpec Shall never be null
	 */
	protected final String description;

	/**
	 * Symbol to express this unit.
	 * E.g. : "Km/h"
	 * @implSpec Shall never be null
	 */
	protected final String symbol;

	/**
	 * A value currently stored in this unit.
	 * In order to be converted to some other units or simply accessed for
	 * display purposes
	 */
	protected Optional<E> value;

	/**
	 * Flag indicating value can be set through {@link #setValue(Comparable)}
	 */
	protected final boolean setable;

	/**
	 * Current Sorting order to apply when comparing units
	 * @see #compareTo(Unit)
	 */
	protected static SortOrder order = SortOrder.NAME_ASCENDING;

	/**
	 * Valued constructor
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol  the symbol used with this unit
	 * @param setable flag indicating if values can be set through {@link #setValue(Comparable)}
	 * @throws NullPointerException if any of the provided values are null
	 * @implNote {@link #value} should be initialized as {@link Optional#empty()}
	 */
	public Unit(MeasureType type,
	            String description,
	            String symbol,
	            boolean setable) throws NullPointerException
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(description);
		Objects.requireNonNull(symbol);
		this.type = type;
		this.description = description;
		this.symbol = symbol;
		value = Optional.empty();
		this.setable = setable;
	}

	/**
	 * Measure type accessor
	 * @return the measure type of this unit
	 */
	public MeasureType getType()
	{
		return type;
	}

	/**
	 * Description accessor
	 * @return the description of this unit
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Symbol accessor
	 * @return the symbol of this unit
	 */
	public String getSymbol()
	{
		return symbol;
	}

	/**
	 * Indicator of an internal present {@link #value}
	 * @return true if {@link #value} contains an actual value
	 */
	public boolean hasValue()
	{
		return value.isPresent();
	}

	/**
	 * Accessor to optional value
	 * @return the stored value (iff available)
	 * @throws NoSuchElementException if there is no such value
	 */
	public E getValue() throws NoSuchElementException
	{
		return value.get();
	}

	/**
	 * Internal value converted to SI value (for conversion purposes)
	 * @return the equivalent SI value
	 * @throws NoSuchElementException if there is no value to convert
	 */
	public abstract double getSIValue() throws NoSuchElementException;

	/**
	 * Setable value accessor
	 * @return true if values can be set with {@link #setValue(Object)}, false
	 * otherwise
	 * @see #setValue(Object)
	 */
	@Override
	public boolean isSetable()
	{
		return setable;
	}

	/**
	 * Value setter
	 * @param value the value to store in optional value
	 * @throws UnsupportedOperationException if values can't be set
	 * @see #isSetable()
	 * @see #setValue_Impl(Comparable)
	 */
	@Override
	public void setValue(E value) throws UnsupportedOperationException
	{
		if (!setable)
		{
			throw new UnsupportedOperationException("values can't be set in "
			    + getClass().getSimpleName());
		}
		setValue_Impl(value);
	}

	/**
	 * Internal Value setter (which can be used to set values internally even if
	 * {@link #setable} is false).
	 * @param value the value to store in optional value
	 */
	protected void setValue_Impl(E value)
	{
		this.value = Optional.ofNullable(value);
		/*
		 * Use this to show actual value so that you can adjust print format later
		 */
//		System.out.println(this + " Setting value = " + this.value.get());
	}

	/**
	 * Clears internal {@link #value}.
	 * Sets {@link #value} as an empty {@link Optional}
	 */
	public void clearValue()
	{
		value = Optional.empty();
	}

	/**
	 * Format the internal value for printing.
	 * e.g. "96.5" in "96.5 Km/h"
	 * @return a formatted String of the internal value or null if formatting
	 * is not possible
	 * @implNote {@link javafx.scene.control.Label} supports null strings
	 * @throws NoSuchElementException if there is no value to format
	 */
	public abstract String formatValue() throws NoSuchElementException;

	/**
	 * Convert value from the provided unit to this unit
	 * @param unit the unit to convert value from
	 * @implSpec {@link #value} will contain converted value from provided unit
	 * iff the provided unit has a value, otherwise
	 * @throws IllegalStateException if the provided unit has no value and
	 * therefore no value can be converted.
	 */
	public abstract void convertValueFrom(Unit<?> unit);

	/**
	 * Convert internal {@link #value} to the provided unit's value
	 * @param unit the unit to convert {@link #value} to.
	 * @throws IllegalStateException if {@link #value} is empty.
	 * @see #convertValueFrom(Unit)
	 */
	public void convertValueTo(Unit<?> unit)
	{
		// TODO Complete Unit::convertValueTo(Unit<?> unit) using unit's convertValueFrom
		if(value.isEmpty()){
			throw new IllegalStateException("value is empty");
		}
		unit.convertValueFrom(this);
		setValue_Impl(this.getValue());
	}

	/**
	 * Get the factor of this unit (for sorting purposes).
	 * Base (SI) units will have a 1.0 factor.
	 * Derived units will have other factors.
	 * @implSpec the factor to apply on {@link #value} of this unit
	 * to get values expressed in the corresponding SI unit.
	 * @return the factor of this unit
	 */
	protected abstract double getFactor();

	/**
	 * Compare this unit to another unit.
	 * Using names {@link Unit#description} and
	 * {@link Unit#symbol} if {@link #order} is {@link SortOrder#NAME_ASCENDING}
	 * or {@link SortOrder#NAME_DESCENDING}. Using {@link #getFactor()} if
	 * {@link #order} is {@link SortOrder#FACTOR_ASCENDING} or
	 * {@link SortOrder#FACTOR_DESCENDING}
	 * @param unit the other unit to compare to
	 * @return -1 if this unit is considered smaller than the provided unit. 0
	 * if both units are considered equals. And 1 otherwise
	 * @throws NullPointerException if the provided unit is null
	 */
	@Override
	public int compareTo(Unit<?> unit)
	    throws NullPointerException
	{
		Objects.requireNonNull(unit);

		int descriptionCompare = description.compareTo(unit.description);
		int symbolCompare = symbol.compareTo(unit.symbol);
		int nameCompare = (descriptionCompare == 0 ? symbolCompare : descriptionCompare);
		double factor = getFactor();
		double otherFactor = unit.getFactor();
		int factorCompare = factor < otherFactor ? -1 : factor == otherFactor ? 0 : 1;
		int result = 0;
		switch (order)
		{
			case NAME_ASCENDING:
				// TODO Complete using nameCompare or factorCompare if nameCompare is inconclusive
				result = nameCompare != 0 ? nameCompare : factorCompare;
				break;
			case NAME_DESCENDING:
				// TODO Complete using -nameCompare or -factorCompare if nameCompare is inconclusive
				result = -(nameCompare != 0 ? nameCompare : factorCompare);
				break;
			case FACTOR_ASCENDING:
				// TODO Complete using factorComparee or nameCompare if factorCompare is inconclusive
				result = factorCompare != 0 ? factorCompare : nameCompare;
				break;
			case FACTOR_DESCENDING:
				// TODO Complete using -factorComparee or -nameCompare if factorCompare is inconclusive
				result = -(factorCompare != 0 ? factorCompare : nameCompare);
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + order);
		}
		return result;
	}

	/**
	 * Partial hash code based on class, {@link #type},
	 * {@link #description} and {@link #symbol}.
	 * @return a partial hash code to be used by subclasses
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + getClass().hashCode();
		result = (prime * result) + type.hashCode();
		result = (prime * result) + description.hashCode();
		result = (prime * result) + symbol.hashCode();
		return result;
	}

	/**
	 * Compare this unit to another object
	 * @param obj the other object to compare to
	 * @return true if the other object is a non null {@link Unit} which
	 * compares to the current {@link Unit} with 0 value and contains the same
	 * description and symbol
	 * @see #compareTo(Unit)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof Unit<?>))
		{
			return false;
		}

		Unit<?> other = (Unit<?>) obj;

		/*
		 * TODO compare with other using:
		 * 	- compareTo
		 * 	- compare types
		 * 	- compare decriptions
		 * 	- compare symbols
		 * We intentionally ignore the class
		 */
		if (compareTo(other) != 0)
		{
			return false;
		}

		if (other.type != type)
		{
			return false;
		}

		if (!other.description.equals(description))
		{
			return false;
		}

		if (!(other.symbol.equals(symbol)))
		{
			return false;
		}

		return true;
	}

	/**
	 * String representation of this unit based on {@link #description} and
	 * {@link #symbol}
	 * @return a new String representation of this unit based on
	 * {@link #description} and {@link #symbol}
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(description);
		if (!symbol.isEmpty())
		{
			builder.append(" (");
			builder.append(symbol);
			builder.append(')');
		}
		return builder.toString();
	}

	/**
	 * Get the current order used to compare Units
	 * @return the current order
	 */
	public static SortOrder getOrder()
	{
		return order;
	}

	/**
	 * Sets a new order to compare units
	 * @param order the order to set
	 */
	public static void setOrder(SortOrder order)
	{
		Unit.order = order;
	}
}
