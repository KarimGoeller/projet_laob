package measures.units;

import java.text.ParseException;
import java.util.Objects;

import measures.MeasureType;

/**
 * Base Numeric unit for measures with bounds (and/or cycles).
 * E.g. : angles in [0..2𝜋[ cyclic or temperatures in [0..infinity[ non cyclic
 * @author davidroussel
 */
public class BoundedBaseNumericUnit extends BaseNumericUnit
    implements Bounded<Double>
{
	/**
	 * Minimum acceptable value
	 */
	protected final Double min;

	/**
	 * Maximum acceptable value
	 */
	protected final Double max;

	/**
	 * Flag indicating values of this unit are cyclic.
	 * Meaning: When setting a value above {@link #max} of below {@link #min}
	 * the provided value shall be normalized between [min..max]
	 * @see #normalize(Double)
	 */
	protected final boolean cyclic;

	/**
	 * Valued constructor
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param format the numeric format to apply when formatting values :
	 * "length.decimals
	 * @param min minimum value of the range
	 * @param max maximum value of the range
	 * @param cyclic flag indicating values in this unit are cyclic (meaning
	 * values over max or lower min shall be normalised between [min..max].
	 * Otherwise, setting a value below minimum acceptable value will result in
	 * setting the minimum value and settting a value above maximum value will
	 * result in setting the maximum value.
	 * @param setable Flag indicating setting values with
	 * {@link #setValue(Double)} is legal
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 */
	protected BoundedBaseNumericUnit(MeasureType type,
	                                 String description,
	                                 String symbol,
	                                 String format,
	                                 Double min,
	                                 Double max,
	                                 boolean cyclic,
	                                 boolean setable)
	    throws NullPointerException,
	    ParseException
	{
		super(type, description, symbol, format, setable);

		Objects.requireNonNull(min);
		Objects.requireNonNull(max);

		/*
		 * TODO Check min < max before assigning to this.min & this.max
		 */
		this.min  = min > max ? max : min;
		this.max = min > max ? min : max;

		this.cyclic = cyclic;
	}

	/**
	 * Valued constructor
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param format the numeric format to apply when formatting values :
	 * "length.decimals
	 * @param min minimum value of the range
	 * @param max maximum value of the range
	 * @param cyclic flag indicating values in this unit are cyclic (meaning
	 * values over max or lower min shall be normalised between [min..max].
	 * Otherwise, setting a value below minimum acceptable value will result in
	 * setting the minimum value and settting a value above maximum value will
	 * result in setting the maximum value.
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 */
	public BoundedBaseNumericUnit(MeasureType type,
	                              String description,
	                              String symbol,
	                              String format,
	                              Double min,
	                              Double max,
	                              boolean cyclic)
	    throws NullPointerException,
	    ParseException
	{
		this(type,
		     description,
		     symbol,
		     format,
		     min,
		     max,
		     cyclic,
		     true);
	}

	/**
	 * Special constructor for units with only min bound.
	 * Max bound will implicitly be set to positive infinity and cyclic flag to
	 * false.
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param format the numeric format to apply when formatting values :
	 * "length.decimals"
	 * @param min minimum value of the range
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 */
	public BoundedBaseNumericUnit(MeasureType type,
	                              String description,
	                              String symbol,
	                              String format,
	                              Double min)
	    throws NullPointerException,
	    ParseException
	{
		this(type,
		     description,
		     symbol,
		     format,
		     min,
		     Double.POSITIVE_INFINITY,
		     false,
		     true);
	}

	/**
	 * Special constructor for units with min = 0 & max = Infinity bounds.
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param format the numeric format to apply when formatting values :
	 * "length.decimals"
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 */
	public BoundedBaseNumericUnit(MeasureType type,
	                              String description,
	                              String symbol,
	                              String format)
	    throws NullPointerException,
	    ParseException
	{
		this(type,
		     description,
		     symbol,
		     format,
		     0.0,
		     Double.POSITIVE_INFINITY,
		     false,
		     true);
	}

	/**
	 * Special constructor for units with min = 0 & max = Infinity bounds and
	 * default format
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 */
	public BoundedBaseNumericUnit(MeasureType type,
	                              String description,
	                              String symbol)
	    throws NullPointerException,
	    ParseException
	{
		this(type,
		     description,
		     symbol,
		     BaseNumericUnit.DefaultFormat,
		     0.0,
		     Double.POSITIVE_INFINITY,
		     false,
		     true);
	}

	/**
	 * Accessor to lower bound
	 * @return the lower bound
	 */
	@Override
	public Double getMin()
	{
		return min;
	}

	/**
	 * Accessor to upper bound
	 * @return the upper bound
	 */
	@Override
	public Double getMax()
	{
		return max;
	}

	/**
	 * Indicator of cyclic values:
	 * All values above maximum value shall be renormalized between [min..max].
	 * @return true if values are cyclic
	 */
	@Override
	public boolean isCyclic()
	{
		return cyclic;
	}

	/**
	 * Normalize provided value inside [min..max]
	 * @param value the value to normalize
	 * @return the normalized value
	 * @see #setValue(Double)
	 */
	@Override
	public Double normalize(Double value)
	{
		Double result = value;

		/*
		 * TODO Ensure result is in [min..max]
		 * If result < min then add max to (result - min) until >= min
		 * If result > max the add min to (result - max) until <= max
		 */
		while(result<min){
			result+=(max - min);
		}
		while (result>max){
			result +=(min - max);
		}

		return result;
	}

	/**
	 * Internal Value setter (which can be used to set values internally even if
	 * {@link #setable} is false).
	 * If {@link #cyclic} is true then values are normalized between
	 * {@link #min} and {@link #max} using {@link #normalize(Double)}.
	 * Otherwise, setting values below {@link #min} will result in setting
	 * value #min and setting values above {@link #max} will result in setting
	 * value {@link #max}.
	 * @param value the value set
	 */
	@Override
	protected void setValue_Impl(Double value)
	{
		Double valueToSet;
		if (cyclic)
		{
			valueToSet = normalize(value);
		}
		else
		{
			if (value < min)
			{
				valueToSet = min;
			}
			else if (value > max)
			{
				valueToSet = max;
			}
			else
			{
				valueToSet = value;
			}
		}
		super.setValue_Impl(valueToSet);
	}
}
