
import java.text.ParseException;

import application.Converter;
import measures.MeasureType;
import measures.units.Unit;

/**
 * Main class for {@link Converter} tests
 * @author davidroussel
 */
public class TestConverter
{

	/**
	 * Converter test program
	 * @param args arguments
	 * @implNote run with -ea VM argument to allow assertions
	 */
	public static void main(String[] args)
	{
		try
		{
			Converter converter = new Converter(MeasureType.LENGTH);

			for (MeasureType type : MeasureType.all())
			{
				converter.setMeasureType(type);
				for (Unit<Double> sUnit : converter.getSourceUnits())
				{
					converter.setSourceUnit(sUnit);
					Unit<Double> sourceUnit = converter.getSourceUnit();
					converter.setInputText("1.0");
					System.out.println("Source = " + sourceUnit + ": "
					    + sourceUnit.formatValue() + " " + sourceUnit.getSymbol());

					for (Unit<Double> dUnit : converter.getdestinationUnits())
					{
						converter.setDestinationUnit(dUnit);
						Unit<Double> destUnit = converter.getDestinationUnit();
						destUnit.convertValueFrom(sourceUnit);
						System.out.println("Destination = " + destUnit + ": "
						    + destUnit.formatValue() + " " + destUnit.getSymbol());
					}
				}
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}

}
