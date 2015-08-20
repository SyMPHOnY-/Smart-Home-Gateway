package cz.vutbr.wislab.symphony.TVScreenGenerator.util;

public interface SlideGenerator {
	
	
	/**
	 * 
	 * @param date Set the time and date in String format DD.MM.YYYY | HH:MM.
	 * @param inputFolder Input folder with background images and symbols
	 * @param outputFolder Output folder for storing of output slides
	 */

	public void initialize (String date);
		
	/**
	 * 
	
	 * @param isChecked Set true, if all components are ready.
	 * @param temperature Set only a value.
	 * @param humidity Set only a value.
	 * @param water Set only a value.
	 * @param isMoreThanAverage Set true, if the water today consumption is bigger than average.
	 * @param isElectricityBalancePlus Set true, if the electricity production is more than consumption. 
	 * @param electricity Set electricity balance - only value (kWh).
	 * @param electrityBalanceEur Set electricity balance - only value (EUR).
	 * @param isArmed Set true, if the alarm system is armed.
	 */
	public void setFirstSlide (			
			boolean isChecked,
			double temperature,
			double humidity,
			double water,
			boolean isMoreThanAverage,
			boolean isElectricityBalancePlus,
			double electricity,
			double  electrityBalanceEur,
			boolean isArmed
			);

	/**
	 * 
	 * @param temperature Set current temperature.
	 * @param minTemperature Set minimum temperature.
	 * @param maxTemperature Set maximum temperature.
	 * @param humidity Set current humidity .
	 * @param minHumidity Set minimum humidity. 
	 * @param maxHumidity Set maximum humidity.
	 */	
	public void setSecondSlide (
			double temperature, 
			double minTemperature, 
			double maxTemperature,
			double humidity,
			double minHumidity,
			double maxHumidity			
			);
	
	/**
	 * 
	 * @param consumption Set today electricity consumption.
	 * @param production Set today electricity production.
	 * @param difference Set difference between electricity consumption and production.
	 * @param eurConsumption Set price in EUR.
	 */	
	
	public void setThirdSlide (
			double consumption,
			double production,
			double difference,
			double eurConsumption			
			);
	
	/**
	 * 
	 * @param waterToday Set today water consumption.
	 * @param waterYesterday Set yesterday water consumption.
	 * @param waterAverageSevenDays Set average water consumption from last 7 days.
	 */
	
	public void setFourthSlide (
			int waterToday,
			int waterYesterday,
			int waterAverageSevenDays
			);
	
	/**
	 * 
	 * @param isArmed Set true, is the alarm is armed.
	 * @param isMessage Set true, if there was some issue with alarm.
	 * @param alertDate Set the issue date and time in String format DD.MM.YYYY | HH:MM
	 * @param alertMessage Set the message about the issue.
	 */
	public void setFifthSlide (
			boolean isArmed,
			boolean isMessage,
			String alertDate,
			String alertMessage
			);
	
}
