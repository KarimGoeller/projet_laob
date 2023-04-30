package measures.units;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Unit composed of multiple values.
 * E.g. (Hours : Minutes : Seconds) or (Degrees, Minutes, Seconds)
 * @author davidroussel
 * @implNote internal {@link Unit#value} is never used in this class since it
 * is not setable
 */
public class DecomposedUnit extends Unit<Double>
{
	/**
	 * The Unit this decomposed unit shall decompose
	 * e.g. degrees for (degrees, minutes, seconds) or hours for (hours :
	 * minutes : seconds)
	 * @implNote Each time this internal unit changes value, {@link #values}
	 * shall be recomputed.
	 */
	private Unit<Double> rawUnit;

	/**
	 * Coefficients to apply at each step of the decomposition
	 */
	private final Double[] coefs;

	/**
	 * The various symbols to attach to each part of the decomposition
	 */
	private final String[] symbols;

	/**
	 * The Separator to separate each part of the decomposed value
	 */
	private final String separator;

	/**
	 * A formatter to format values (using "00" as format string)
	 */
	private final DecimalFormat formatter;

	/**
	 * Decomposed Unit
	 * @param unit the unit to decompose
	 * @param description the description of this unit
	 * @param symbols The symbol string containing symbols separated by separator.
	 * Needs to be parsed according to separator : e.g. "h:m:s"
	 * @param separator the separator to print between decomposed values and
	 * symbols.
	 * @param coefs The coefficients to apply at each step of the decomposition
	 * @throws IllegalArgumentException if symbols and coefs do not have the
	 * same number of elements.
	 */
	public DecomposedUnit(Unit<Double> unit,
	                      String description,
	                      String symbols,
	                      String separator,
	                      Double[] coefs)
	{
		super(unit.getType(), description, symbols, false);
		this.separator = separator;
		this.symbols = parseSymbols(symbols, separator);
		if (this.symbols.length != coefs.length)
		{
			throw new IllegalArgumentException("symbols [" + this.symbols.length
			    + "] and coefs[" + coefs.length
			    + "] don't have the same number of elements");
		}
		rawUnit = unit;
		this.coefs = coefs;
		formatter = new DecimalFormat("00");
	}

	/**
	 * Parse symbols string (e.g. "h:m:s") to produce an array containing each symbol ("h, "m, "s")
	 * @param symbols the symbol string to parse
	 * @param separator the separator contained in the symbols string
	 * @return an array of symbols
	 */
	private String[] parseSymbols(String symbols, String separator)
	{
		List<Integer> indexes = new ArrayList<Integer>();
		int sepLength = separator.length();
		int index = 0;
		while (index < (symbols.length() - sepLength))
		{
			String sub = symbols.substring(index, index + sepLength);
			if (separator.equals(sub))
			{
				indexes.add(index);
			}
			index++;
		}
		if (!indexes.isEmpty())
		{
			String[] symbolsArray = new String[indexes.size() + 1];
			int start = 0;

			for (int i = 0; i < (indexes.size() + 1); i++)
			{
				int stop;
				if (i < indexes.size())
				{
					stop = indexes.get(i);
				}
				else
				{
					stop = symbols.length();
				}
				symbolsArray[i] = symbols.substring(start, stop);
				start = stop + sepLength;
			}
			return symbolsArray;
		}
		return null;
	}

	/**
	 * Indicator of an internal present {@link #value} in {@link #rawUnit}
	 * @return true if {@link #rawUnit} contains an actual value
	 */
	@Override
	public boolean hasValue()
	{
		/*
		 * TODO use #rawUnit to determine if we have a Value
		 */

		return rawUnit.hasValue();
	}

	/**
	 * Accessor to optional value of internal {@link #rawUnit}
	 * @return the stored value (iff available)
	 * @throws NoSuchElementException if there is no such value
	 */
	@Override
	public Double getValue() throws NoSuchElementException
	{
		/*
		 * TODO use #rawUnit to get value
		 */
		Double res = rawUnit.getValue();
		if(res == null){
			throw new NoSuchElementException("no internal value");
		}
		return res;
	}

	/**
	 * Internal value converted to SI value (for conversion purposes)
	 * @return the equivalent SI value
	 * @throws NoSuchElementException if there is no value to convert
	 */
	@Override
	public double getSIValue() throws NoSuchElementException
	{
		/*
		 * TODO use #rawUnit to get SI value
		 */
		return rawUnit.getSIValue();
	}

	/**
	 * Symbol accessor (reimplemented to return empty string).
	 * The {@link #formatValue()} already formats symbols
	 * @return an empty string
	 */
	@Override
	public String getSymbol()
	{
		return "";
	}

	/**
	 * Get the factor of this unit (for sorting purposes).
	 * @return the factor of this unit
	 */
	@Override
	protected double getFactor()
	{
		/*
		 * TODO use #rawUnit to get factor
		 */
		return rawUnit.getFactor();
	}

	/**
	 * Format the internal {@link #values} for printing.
	 * e.g. "45 : 54 : 56"
	 * @return a formatted String of the internal value or null if formatting
	 * is not possible
	 * @throws NoSuchElementException if there is no value to format
	 */
	@Override
	public String formatValue() throws NoSuchElementException
	{
		StringBuilder builder = new StringBuilder();
		double dValue = rawUnit.getValue();
		double iValue;
		for (int i = 0; i < coefs.length; i++)
		{
			dValue *= coefs[i];
			iValue = Math.floor(dValue);
			builder.append(formatter.format(iValue));
			dValue -= iValue;
			builder.append(symbols[i]);
			if (i < (coefs.length - 1))
			{
				builder.append(" ");
				builder.append(separator);
				builder.append(" ");
			}
		}
		return builder.toString();
	}

	/**
	 * Convert value from the provided unit to this unit
	 * @param unit the unit to convert value from
	 * @implSpec {@link #value} will contain converted value from provided unit
	 * iff the provided unit has a value, otherwise
	 * @throws IllegalStateException if the provided unit has no value and
	 * therefore no value can be converted.
	 */
	@Override
	public void convertValueFrom(Unit<?> unit)
	{
		/*
		 * TODO use #rawUnit to convert value from unit
		 */
		rawUnit.convertValueFrom(unit);
	}
}
