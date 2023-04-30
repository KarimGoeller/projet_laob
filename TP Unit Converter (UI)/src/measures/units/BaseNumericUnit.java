package measures.units;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.NoSuchElementException;
import java.util.Objects;

import measures.MeasureType;

/**
 * Base Numeric unit for all SI units containing numerical values
 * @author davidroussel
 */
public abstract class BaseNumericUnit extends Unit<Double>
{
	/**
	 * The decimal formatter used to format values
	 * @see #formatValue(Double)
	 */
	protected final DecimalFormat formatter;

	/**
	 * Default numeric format
	 */
	public final static String DefaultFormat  = "7.2";

	/**
	 * Valued constructor
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param format the numeric format to apply when formatting values :
	 * "length.decimals". format can be empty but can't be null.
	 * @param setable flag indicating this unit can set values with
	 * {@link #setValue(Double)}
	 * @throws NullPointerException if any of the provided values are null
	 * @throws ParseException if the format string cannot be parsed.
	 */
	protected BaseNumericUnit(MeasureType type,
	                          String description,
	                          String symbol,
	                          String format,
	                          boolean setable)
	    throws NullPointerException,
	    ParseException
	{
		super(type, description, symbol, setable);
		Objects.requireNonNull(format);

		formatter = format.isEmpty() ? null : parseFormatString(format);
	}

	/**
	 * Internal value converted to SI value (for conversion purposes).
	 * In this particular case SI value IS value
	 * @return the internal {@link Unit#value} which is equivalent SI value
	 * @throws NoSuchElementException if there is no value to convert
	 */
	@Override
	public double getSIValue() throws NoSuchElementException
	{
		// TODO complete BaseNumericUnit::getSIValue();
		if(value == null){
			throw new NoSuchElementException("internal value is null");
		}
		return value.get();
	}

	/**
	 * Get the factor of this unit (for sorting purposes).
	 * Base (SI) units will have a 1.0 factor.
	 * Derived units will have other factors
	 * @implSpec the factor to apply to convert from SI Unit to this unit.
	 * @return the factor of this unit
	 */
	@Override
	protected double getFactor()
	{
		return 1.0;
	}

	/**
	 * Format the internal value for printing using {@link #formatter}
	 * e.g. "96.5" in "96.5 Km/h"
	 * @return a formatted String of the internal value or null if
	 * {@link #formatter} is null or {@link Unit#value} is empty.
	 * @throws NullPointerException if the {@link #formatter} is null
	 * @throws NoSuchElementException if there is no value to format
	 * @see DecimalFormat#format(double)
	 */
	@Override
	public String formatValue()
		throws NullPointerException,
		NoSuchElementException
	{
		// TODO Complete BaseNumericUnit::formatValue() using formatter
		if (value == null) {
			throw new NoSuchElementException("Internal value is null");
		}
		if (formatter == null) {
			throw new NullPointerException("Formatter is null");
		}
		return formatter.format(value.get());
	}

	/**
	 * Creates a new {@link DecimalFormat} based on the provided format string.
	 * e.g. "+a.b" where
	 * <ul>
	 * <li>"+" [Optional] indicates sign must be placed before values</li>
	 * <li>"a" indicates the total number of digits to use</li>
	 * <li>"b" [Optional] indicates the number of decimal places</li>
	 * <li>a &gt;= b + 2</li>
	 * </ul>
	 * @param format the format string used to build the {@link DecimalFormat}
	 * @return a new {@link DecimalFormat} based on provided format
	 * @throws ParseException if the format string can not be parsed
	 */
	protected static DecimalFormat parseFormatString(String format)
	    throws ParseException
	{
		/*
		 * Format elements
		 */
		boolean sign = false;
		int length = 4;
		int decimal = 0;
		int currentIndex = 0;
		int minLength = 2;

		/*
		 * Check for sign
		 */
		if (format.charAt(currentIndex) == '+')
		{
			sign = true;
			// System.out.println("\tSign on");
			currentIndex++;
			minLength++;
		}

		/*
		 * Check for total length
		 */
		try
		{
			length = Integer.parseInt(format.substring(currentIndex,
			                                           format.indexOf(".")));
		}
		catch (NumberFormatException nfe)
		{
			throw new ParseException(format + ": Error parsing length value",
			                         currentIndex);
		}
		catch (StringIndexOutOfBoundsException sie)
		{
			throw new ParseException(format + ": Error in String indexes",
			                         currentIndex);
		}

		/*
		 * Check for decimal length
		 */
		currentIndex = format.indexOf(".") + 1;
		try
		{
			decimal = Integer.parseInt(format.substring(currentIndex,
			                                            format.length()));
		}
		catch (NumberFormatException nfe)
		{
			throw new ParseException(format + ": Error parsing decimal value",
			                         currentIndex);
		}
		catch (StringIndexOutOfBoundsException sie)
		{
			throw new ParseException(format + ": Error in String indexes",
			                         currentIndex);
		}

		/*
		 * Check for total length vs decimal length (with or without sign)
		 * consistency
		 */
		if (decimal > (length - minLength))
		{
			decimal = length - minLength;
			System.err.println("\"" + format
			    + "\" : Error decimal too long, reducing to : " + decimal);
		}

		/*
		 * Build DecimalFormat based on Format elements
		 */
		StringBuilder patternBuilder = new StringBuilder();
		// adds "+" sign (if required)
		int integralPart = length - decimal - 1;
		if (sign)
		{
			patternBuilder.append("+");
			integralPart--;
		}
		// Integral part digits
		for (int j = 0; j < integralPart; j++)
		{
			patternBuilder.append("#");
		}
		// Decimal part digits
		if (decimal > 0)
		{
			patternBuilder.append(".");
			for (int j = 0; j < decimal; j++)
			{
				patternBuilder.append("0");
			}
		}

		/*
		 * If sign is used then the dual pattern for negative numbers must be
		 * provided starting with a ";-"
		 */
		if (sign)
		{
			patternBuilder.append(";-");
			for (int j = 0; j < integralPart; j++)
			{
				patternBuilder.append("#");
			}
			if (decimal > 0)
			{
				patternBuilder.append(".");
				for (int j = 0; j < decimal; j++)
				{
					patternBuilder.append("0");
				}
			}
		}

		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		dfs.setDecimalSeparator('.');
		return new DecimalFormat(patternBuilder.toString(), dfs);
	}

	/**
	 * Convert value from the provided unit to this unit
	 * @param unit the unit to convert value from
	 * @implSpec {@link #value} will contain converted value from provided unit
	 * iff the provided unit has a value, otherwise
	 * @throws IllegalStateException if the provided unit has no value and
	 * therefore no value can be converted.
	 * @see #setValue_Impl(Double)
	 */
	@Override
	public void convertValueFrom(Unit<?> unit)
	{
		if (!unit.hasValue())
		{
			throw new IllegalStateException("provided unit has no value");
		}
		// TODO Complete BaseNumericUnit::convertValueFrom(Unit<?> unit) using unit SI value
		double src = unit.getSIValue();
		double fact = unit.getFactor();

		double val = src * fact;
		setValue_Impl(val);

	}
}
