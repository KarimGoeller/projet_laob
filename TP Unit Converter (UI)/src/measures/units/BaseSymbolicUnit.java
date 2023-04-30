package measures.units;

import java.text.ParseException;
import java.util.Arrays;

import measures.MeasureType;

/**
 * Special case of bounded base numeric unit.
 * Symbolic Unit provide formatted values as symbols (on a scale) rather than
 * numbers.
 * Base Symbolic Units have
 * <ul>
 * 	<li>internal SI unit value.</li>
 * 	<li>no format since it is not required to print symbols.</li>
 * 	<li>no min nor max values since they will be deducted from range values</li>
 * </ul>
 * @author davidroussel
 */
public class BaseSymbolicUnit extends BoundedBaseNumericUnit
{
	/**
	 * Values defining ranges for each symbol in {@link #symbols}
	 */
	protected double[] values;

	/**
	 * Symbols associated to each values pairs.
	 */
	protected String[] symbols;

	/**
	 * Valued constructor
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param values values associated to each symbol. First falue is also min
	 * and last value is max.
	 * @param symbols symbols associated to values. N symbols require N+1 values
	 * in order to have 1 values interval per symbol.
	 * @param cyclic flag indicating values in this unit are cyclic (meaning
	 * values over max or lower min shall be normalised between [min..max].
	 * Otherwise, setting a value below minimum acceptable value will result in
	 * setting the minimum value and settting a value above maximum value will
	 * result in setting the maximum value.
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws IllegalArgumentException if values and symbols don't contain N+1
	 * and N values respectively.
	 * @throws ParseException if empty format can't be parsed (very unlikely)
	 */
	public BaseSymbolicUnit(MeasureType type,
	                        String description,
	                        String symbol,
	                        double[] values,
	                        String[] symbols,
	                        boolean cyclic)
	    throws NullPointerException, IllegalArgumentException, ParseException
	{
		super(type,
		      description,
		      symbol,
		      "",
		      values[0],
		      values[values.length - 1],
		      cyclic,
		      false);	// Symbolic units are NOT setable
		/*
		 * TODO Assert Values contains increasing values (otherwise throw IllegalArgumentException)
		 */
		Arrays.stream(values)
				.reduce((prev, curr) -> {
					if (curr < prev) {
						throw new IllegalArgumentException("Values must be increasing");
					}
					return curr;
				});
		/*
		 * TODO Assert symbols have exactly 1 more elt than values (otherwise throw IllegalArgumentException)
		 */
		if (symbols.length != values.length + 1) {
			throw new IllegalArgumentException("Symbols must have exactly 1 more element than values");
		}

		this.values = values;
		this.symbols = symbols;
	}

	/**
	 * Computes the symbol to return according to internal value
	 * {@link Unit#value}
	 * @return the symbol in {@link #symbols} corresponding to the internal
	 * value's interval in {@link #values} or null if there is no such symbol.
	 * @throws IllegalStateException if the internal value is not in the rangle
	 * of {@link #values}
	 */
	@Override
	public String formatValue()
	{
		double ivalue = value.get();
		if ((ivalue < values[0]) ||
			(ivalue > values[values.length - 1]))
		{
			throw new IllegalStateException("value out of range");
		}
		for (int i = 0; i < symbols.length; i++)
		{
			if ((ivalue >= values[i]) && (ivalue < values[i+1]))
			{
				return symbols[i];
			}
		}
		return symbols[symbols.length - 1];
	}
}
