import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import measures.MeasureType;
import measures.units.BaseSymbolicUnit;
import measures.units.BoundedBaseNumericUnit;
import measures.units.DecomposedUnit;
import measures.units.DerivedNumericUnit;
import measures.units.DerivedSymbolicUnit;
import measures.units.OperationOrder;
import measures.units.Unit;

/**
 * Main class for {@link Unit}s tests
 * @author davidroussel
 */
public class TestUnits
{
	/**
	 * Units test program
	 * @param args arguments
	 * @implNote run with -ea VM argument to allow assertions
	 */
	public static void main(String[] args)
	{
		try
		{
			BoundedBaseNumericUnit mpsSpeed =
			    new BoundedBaseNumericUnit(MeasureType.SPEED,
			                               "Mètres / seconde",
			                               "m/s");
			assert(!mpsSpeed.hasValue());
			mpsSpeed.setValue(-45.7);
			assert(mpsSpeed.getValue() == 0.0);
			assert(mpsSpeed.getValue().equals(mpsSpeed.getSIValue()));
			assert(mpsSpeed.getValue() == mpsSpeed.getSIValue());

			BoundedBaseNumericUnit angle =
			    new BoundedBaseNumericUnit(MeasureType.DIRECTION,
			                               "angles",
			                               "",
			                               "7.2",
			                               0.0,
			                               (Math.PI * 2.0),
			                               true);
			assert(!angle.hasValue());
			angle.setValue(12.57 * Math.PI);
			assert(Math.abs(angle.getValue() - (0.57 * Math.PI)) < 1e-9);
			angle.setValue(-Math.PI);
			assert(angle.getValue() == Math.PI);

			DerivedNumericUnit kmphSpeed =
			    new DerivedNumericUnit(MeasureType.SPEED,
			                           "Kilomètres / heure",
			                           "km/h",
			                           "5.1",
			                           1.0,
			                           1.0 / 3.6,
			                           0.0,
			                           OperationOrder.FACTOR_ONLY);
			mpsSpeed.setValue(3.0); // 25 m/s == 90 Km/h;
			kmphSpeed.convertValueFrom(mpsSpeed);
			assert(Math.abs(kmphSpeed.getValue() - (mpsSpeed.getValue() * 3.6)) < 1e-6);
			String[] beaufortSymbols = new String[] {
				"Calme",				// [0.0 .. 0.28]
				"Très légère brise",	// [0.28 .. 1.53]
				"Légère brise",			// [1.53 .. 3.19]
				"Petite brise",			// [3.19 .. 5.42]
				"Jolie brise",			// [5.42 .. 7.92]
				"Bonne brise",			// [7.92 .. 10.69]
				"Vent frais",			// [10.69 .. 13.75]
				"Grand frais",			// [13.75 .. 17.08]
				"Coup de vent",			// [17.08 .. 20.69]
				"Fort coup de vent",	// [20.69 .. 24.31]
				"Tempête",				// [24.31 .. 28.47]
				"Violente tempête",		// [28.47 .. 32.64]
				"Ouragan"				// [32.64 .. Infinity]
			};
			double[] beaufortValues = new double [] {
				0.0,
				0.28,
				1.53,
				3.19,
				5.42,
				7.92,
				10.69,
				13.75,
				17.08,
				20.69,
				24.31,
				28.47,
				32.64,
				Double.POSITIVE_INFINITY
			};
			/*
			 * Values used in Beaufort values are SI units:
			 * Conversions parameters are therefore 1.0, 1.0, 0.0 because
			 * internal values are expressed in SI Units
			 */
			BaseSymbolicUnit beaufortScale1 =
				new BaseSymbolicUnit(MeasureType.SPEED,
				                     "Beaufort Symbolique 1",
				                     "",
				                     beaufortValues,
				                     beaufortSymbols,
				                     false);
			beaufortScale1.convertValueFrom(kmphSpeed);

			DerivedNumericUnit beaufortSpeed =
			    new DerivedNumericUnit(MeasureType.SPEED,
			                           "Beaufort Numérique",
			                           "Bf",
			                           "3.1",
			                           0.0,
			                           12.0,
			                           false,
			                           3.0 / 2.0,
			                           0.83,
			                           0.0,
			                           OperationOrder.FACTOR_ONLY);
			beaufortSpeed.convertValueFrom(kmphSpeed);

			DerivedSymbolicUnit beaufortScale2 =
			    new DerivedSymbolicUnit(MeasureType.SPEED,
			                            "Beaufort Symbolique 2",
			                            "",
			                            beaufortSymbols,
			                            false,
			                            3.0 / 2.0,
			                            0.83,
			                            0.0,
			                            OperationOrder.FACTOR_ONLY);
			beaufortScale2.convertValueFrom(kmphSpeed);
			DerivedNumericUnit minPerKmSpeed =
				new DerivedNumericUnit(MeasureType.SPEED,
				                       "Minutes / Kilomètre",
				                       "min/km",
				                       "6.2",
				                       -1.0,
				                       100.0/6.0,
				                       0,
				                       OperationOrder.FACTOR_ONLY);
			minPerKmSpeed.convertValueFrom(mpsSpeed);
			DecomposedUnit msPerKmSpeed =
				new DecomposedUnit(minPerKmSpeed,
				                   "Minutes : Secondes / Kilomètre",
				                   "m:s / km",
				                   ":",
				                   new Double[] {1.0, 60.0});
			msPerKmSpeed.convertValueFrom(mpsSpeed);

			System.out.println(mpsSpeed + ": " + mpsSpeed.formatValue());
			System.out.println(kmphSpeed + ": " + kmphSpeed.formatValue());
			System.out.println(beaufortScale1 + ": " + beaufortScale1.formatValue());
			System.out.println(beaufortSpeed + ": " + beaufortSpeed.formatValue());
			System.out.println(beaufortScale2 + ": " + beaufortScale2.formatValue());
			System.out.println(minPerKmSpeed + ": " + minPerKmSpeed.formatValue());
			System.out.println(msPerKmSpeed + ": " + msPerKmSpeed.formatValue());

			System.out.println("----------------------------");
			mpsSpeed.setValue(100.0);
			kmphSpeed.convertValueFrom(mpsSpeed);
			beaufortScale1.convertValueFrom(mpsSpeed);
			beaufortSpeed.convertValueFrom(mpsSpeed);
			beaufortScale2.convertValueFrom(mpsSpeed);
			minPerKmSpeed.convertValueFrom(mpsSpeed);
			msPerKmSpeed.convertValueFrom(mpsSpeed);

			System.out.println(mpsSpeed + ": " + mpsSpeed.formatValue());
			System.out.println(kmphSpeed + ": " + kmphSpeed.formatValue());
			System.out.println(beaufortScale1 + ": " + beaufortScale1.formatValue());
			System.out.println(beaufortSpeed + ": " + beaufortSpeed.formatValue());
			System.out.println(beaufortScale2 + ": " + beaufortScale2.formatValue());
			System.out.println(minPerKmSpeed + ": " + minPerKmSpeed.formatValue());
			System.out.println(msPerKmSpeed + ": " + msPerKmSpeed.formatValue());

			BoundedBaseNumericUnit secondsTime =
				new BoundedBaseNumericUnit(MeasureType.TIME, "seconds", "s");
			secondsTime.setValue(3727.2);
			DecomposedUnit hmsTime =
				new DecomposedUnit(secondsTime,
				                   "hours:minutes:seconds",
				                   "h:m:s", ":",
				                   new Double[] {1.0/3600.0, 60.0, 60.0});
			hmsTime.convertValueFrom(secondsTime);
			DecomposedUnit msTime =
				new DecomposedUnit(secondsTime,
				                   "minutes:seconds",
				                   "m:s", ":",
				                   new Double[] {1.0/60.0, 60.0});
			msTime.convertValueFrom(secondsTime);
			DecomposedUnit hmscTime =
				new DecomposedUnit(secondsTime,
				                   "hours:minutes:seconds:milliseconds",
				                   "h:m:s:ms", ":",
				                   new Double[] {1.0/3600.0, 60.0, 60.0, 1000.0});
			hmscTime.convertValueFrom(secondsTime);

			System.out.println(secondsTime + " = " + secondsTime.formatValue());
			System.out.println(hmsTime + " = " + hmsTime.formatValue());
			System.out.println(msTime + " = " + msTime.formatValue());
			System.out.println(hmscTime + " = " + hmscTime.formatValue());

			System.out.println("Units basic tests Ok");
		}
		catch (NullPointerException | ParseException e)
		{
			e.printStackTrace();
		}

		double d = 1234567.89;
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
//		dfs.setMonetaryDecimalSeparator('.');
		DecimalFormat df1 = new DecimalFormat("#.##", dfs);
		DecimalFormat df2 = new DecimalFormat("0.00", dfs);
		assert(df1.format(d)).equals("1234567.89");
		assert(df2.format(d)).equals("1234567.89");
	}
}
