package measures.units;

import java.text.ParseException;
import java.util.Arrays;
import java.util.NoSuchElementException;

import measures.MeasureType;

/**
 * Derived Unit from {@link BoundedBaseNumericUnit}.
 * Derived Units are not SI units but can ben converted to SI units
 * @author davidroussel
 */
public class DerivedNumericUnit extends BoundedBaseNumericUnit
{
	/**
	 * The power to apply to values in SI unit to convert to this unit
	 */
	protected double power;

	/**
	 * The Factor to apply to values in SI unit to convert ot this unit
	 */
	protected double factor;

	/**
	 * The offset to apply to values in SI unit to convert to this unit
	 */
	protected double offset;

	/**
	 * The order in which factor and offset operations should be carried out
	 * when converting from or to SI unit
	 * @implNote Knowing that power is ALWAYS apply first
	 */
	protected OperationOrder order;

	/**
	 * Protected Valued constructor with all arguments (dedicated to be used
	 * internally and / or in subclasses)
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param format the numeric format to apply when formatting values :
	 * "length.decimals"
	 * @param min minimum value of the range
	 * @param max maximum value of the range
	 * @param cyclic flag indicating values in this unit are cyclic (meaning
	 * values over max or lower min shall be normalised between [min..max].
	 * Otherwise, setting a value below minimum acceptable value will result in
	 * setting the minimum value and settting a value above maximum value will
	 * result in setting the maximum value.
	 * @param setable Flag indicating setting values with
	 * {@link #setValue(Double)} is legal
	 * @param power the power to apply on values from SI unit to convert to this
	 * unit values
	 * @param factor the factor to apply on values from SI unit to convert to
	 * this unit values
	 * @param offset the offset to apply on values from SI unit to convert to
	 * this unit values
	 * @param order The order in which factor and offset should be applied
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 * @throws IllegalArgumentException if power or factor are too close to 0
	 * (let's say within 10^-6)
	 */
	protected DerivedNumericUnit(MeasureType type,
	                             String description,
	                             String symbol,
	                             String format,
	                             Double min,
	                             Double max,
	                             boolean cyclic,
	                             boolean setable,
	                             double power,
	                             double factor,
	                             double offset,
	                             OperationOrder order)
	    throws NullPointerException,
	    ParseException,
	    IllegalArgumentException
	{
		super(type, description, symbol, format, min, max, cyclic, setable);
		/*
		 * TODO Assert |power| is not lower than 1e-6 otherwise throw IllegalArgumentException
		 */
		if(Math.abs(power)<1e-6){
			throw new IllegalArgumentException("power is null");
		}
		/*
		 * TODO Assert |factor| is not lower than 1e-6 otherwise throw IllegalArgumentException
		 */
		if(Math.abs(factor)<1e-6){
			throw new IllegalArgumentException("power is null");
		}
		this.power = power;
		this.factor = factor;
		this.offset = offset;
		this.order = order;
	}

	/**
	 * Valued constructor
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param format the numeric format to apply when formatting values :
	 * "length.decimals"
	 * @param min minimum value of the range
	 * @param max maximum value of the range
	 * @param cyclic flag indicating values in this unit are cyclic (meaning
	 * values over max or lower min shall be normalised between [min..max].
	 * Otherwise, setting a value below minimum acceptable value will result in
	 * setting the minimum value and settting a value above maximum value will
	 * result in setting the maximum value.
	 * @param power the power to apply on values from SI unit to convert to this
	 * unit values
	 * @param factor the factor to apply on values from SI unit to convert to
	 * this unit values
	 * @param offset the offset to apply on values from SI unit to convert to
	 * this unit values
	 * @param order The order in which factor and offset should be applied
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 * @throws IllegalArgumentException if power or factor are too close to 0
	 * (let's say within 10^-6)
	 */
	public DerivedNumericUnit(MeasureType type,
	                          String description,
	                          String symbol,
	                          String format,
	                          Double min,
	                          Double max,
	                          boolean cyclic,
	                          double power,
	                          double factor,
	                          double offset,
	                          OperationOrder order)
	    throws NullPointerException,
	    ParseException,
	    IllegalArgumentException
	{
		this(type,
		     description,
		     symbol,
		     format,
		     min,
		     max,
		     cyclic,
		     true,
		     power,
		     factor,
		     offset,
		     order);
	}

	/**
	 * Constructor with implicit {@link Double#POSITIVE_INFINITY} max
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol  the symbol used with this unit
	 * @param format the numeric format to apply when formatting values : "length.decimals"
	 * @param min minimum value of the range
	 * @param power the power to apply on values from SI unit to convert to this unit values
	 * @param factor the factor to apply on values from SI unit to convert to this unit values
	 * @param offset the offset to apply on values from SI unit to convert to this unit values
	 * @param order The order in which factor and offset should be applied
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 * @throws IllegalArgumentException if power or factor too close to 0 (let's say within 10^-6)
	 */
	public DerivedNumericUnit(MeasureType type,
	                          String description,
	                          String symbol,
	                          String format,
	                          Double min,
	                          double power,
	                          double factor,
	                          double offset,
	                          OperationOrder order)
	    throws NullPointerException,
	    ParseException,
	    IllegalArgumentException
	{
		this(type,
		     description,
		     symbol,
		     format,
		     min,
		     Double.POSITIVE_INFINITY,
		     false,
		     power,
		     factor,
		     offset,
		     order);
	}

	/**
	 * Constructor with implicit 0 min and {@link Double#POSITIVE_INFINITY} max
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol  the symbol used with this unit
	 * @param format the numeric format to apply when formatting values : "length.decimals"
	 * @param power the power to apply on values from SI unit to convert to this unit values
	 * @param factor the factor to apply on values from SI unit to convert to this unit values
	 * @param offset the offset to apply on values from SI unit to convert to this unit values
	 * @param order The order in which factor and offset should be applied
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 * @throws IllegalArgumentException if power or factor too close to 0 (let's say within 10^-6)
	 */
	public DerivedNumericUnit(MeasureType type,
	                          String description,
	                          String symbol,
	                          String format,
	                          double power,
	                          double factor,
	                          double offset,
	                          OperationOrder order)
	    throws NullPointerException,
	    ParseException,
	    IllegalArgumentException
	{
		this(type,
		     description,
		     symbol,
		     format,
		     0.0,
		     Double.POSITIVE_INFINITY,
		     false,
		     power,
		     factor,
		     offset,
		     order);
	}

	/**
	 * Internal value converted to SI value (for conversion purposes).
	 * @return the internal {@link Unit#value} which is equivalent SI value
	 * @throws NoSuchElementException if there is no value to convert
	 * @see OperationOrder#toSI(double, double, double, double)
	 */
	@Override
	public double getSIValue() throws NoSuchElementException
	{
		/*
		 * TODO Use OperationOrder order operations to return correct SI value
		 */
		if (value == null) {
			throw new NoSuchElementException("No value to convert");
		}

		OperationOrder[] operation = OperationOrder.values();
		double result = Arrays.stream(operation)
				.mapToDouble(op -> op.toSI(value.get(), factor, offset, power))
				.reduce(0.0, (a, b) -> b);
		return result;
	}

	/**
	 * Get the factor of this unit (for sorting purposes).
	 * @implSpec the factor to apply to convert from SI Unit to this unit.
	 * @return the factor of this unit
	 */
	@Override
	protected double getFactor()
	{
		return factor;
	}

	/**
	 * Convert value from the provided unit to this unit
	 * @param unit the unit to convert value from
	 * @implSpec {@link #value} will contain converted value from provided unit
	 * iff the provided unit has a value, otherwise
	 * @throws IllegalStateException if the provided unit has no value and
	 * therefore no value can be converted.
	 * @see OperationOrder#fromSI(double, double, double, double)
	 */
	@Override
	public void convertValueFrom(Unit<?> unit) {
		if (!unit.hasValue()) {
			throw new IllegalStateException("provided unit has no value");
		}

		/*
		 * TODO Convert from SI value using:
		 * 	- the SI value of "unit"
		 * 	- converted using OperationOrder order operations to
		 * 	- setValue_Impl
		 */
		OperationOrder[] operations = OperationOrder.values();
		double src = unit.getSIValue();

		double result = Arrays.stream(operations)
				.mapToDouble(op -> op.fromSI(src, factor, offset, power))
				.reduce(0.0, (a, b) -> b);
		setValue_Impl(result);
	}
}
