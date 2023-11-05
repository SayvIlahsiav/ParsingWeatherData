/**
 * Write a description of ParsingWeatherData here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import edu.duke.*;
import java.util.*;
import org.apache.commons.csv.*;
import java.io.*;

public class ParsingWeatherData 
{
    public CSVRecord lowestOfTwo(CSVRecord currentRow, CSVRecord lowestSoFar, String column)
    {
        if (lowestSoFar == null)
        {
            if (!currentRow.get(column).equals("N/A")) 
                lowestSoFar = currentRow;
        }
        else
        {
            if(!currentRow.get(column).equals("N/A"))
            {
                double current = Double.parseDouble(currentRow.get(column));
                double lowest = Double.parseDouble(lowestSoFar.get(column));
                if(current < lowest && current != -9999)
                    lowestSoFar= currentRow;
            }
        }
        return lowestSoFar;
    }
    
    public CSVRecord coldestHourInFile(CSVParser parser)
    {
        CSVRecord lowestSoFar = null;
        for (CSVRecord currentRow : parser)
        {
            lowestSoFar = lowestOfTwo(currentRow, lowestSoFar, "TemperatureF");
        }
        return lowestSoFar;
    }
    
    public String coldestDayInFiles()
    {
        CSVRecord lowestSoFar = null;
        DirectoryResource dr = new DirectoryResource();
        String coldestFileName = null;
        String coldestYear = "";
        for (File f : dr.selectedFiles())
        {
            String name = f.getName();
            String year = name.substring(8, 12);
            FileResource fr = new FileResource(f);
            CSVRecord currentRow = coldestHourInFile(fr.getCSVParser());
            lowestSoFar = lowestOfTwo(currentRow, lowestSoFar, "TemperatureF");
            if (lowestSoFar == currentRow) 
            {
                coldestFileName = f.getName();
                coldestYear = year;
            }
        }
        return coldestFileName.isEmpty() ? "" : "nc_weather/" + coldestYear + "/" + coldestFileName;
    }
    
    public CSVRecord lowestHumidityInFile(CSVParser parser)
    {
        CSVRecord lowestSoFar = null;
        for (CSVRecord currentRow : parser)
        {
            lowestSoFar = lowestOfTwo(currentRow, lowestSoFar, "Humidity");
        }
        return lowestSoFar;
    }
    
    public CSVRecord lowestHumidityInFiles()
    {
        CSVRecord lowestSoFar = null;
        DirectoryResource dr = new DirectoryResource();
        String fileName = null;
        for (File f : dr.selectedFiles())
        {
            FileResource fr = new FileResource(f);
            CSVRecord currentRow = lowestHumidityInFile(fr.getCSVParser());
            lowestSoFar = lowestOfTwo(currentRow, lowestSoFar, "Humidity");
        }
        return lowestSoFar;
    }
    
    public double  averageTemperatureInFile(CSVParser parser)
    {
        double sumTemp = 0.0;
        int tempCount = 0;
        for (CSVRecord currentRow : parser)
        {
            tempCount++;
            double currentTemp = Double.parseDouble(currentRow.get("TemperatureF"));
            if (currentTemp != -9999)
                sumTemp += currentTemp;
        }
        return sumTemp/tempCount;
    }
    
    public double averageTemperatureWithHighHumidityInFile(CSVParser parser, int value)
    {
        double sumTemp = 0.0;
        int humCount = 0;
        for (CSVRecord currentRow : parser)
        {
            if(!currentRow.get("Humidity").equals("N/A"))
            {
                int current = Integer.parseInt(currentRow.get("Humidity"));
                double currentTemp = Double.parseDouble(currentRow.get("TemperatureF"));
                if(current >= value && currentTemp != -9999)
                {
                    humCount++;
                    sumTemp += currentTemp;
                }    
            }
        }
        if (humCount == 0)
            return 0.0;
        else
            return sumTemp/humCount;
    }
    
    public void testColdestHourInFile()
    {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        CSVRecord lowest = coldestHourInFile(parser);
        String time = "";
        if (lowest.isMapped("TimeEST") && lowest.get("TimeEST") != null) 
            time = lowest.get("TimeEST");
        else if (lowest.isMapped("TimeEDT") && lowest.get("TimeEDT") != null) 
            time = lowest.get("TimeEDT");
        else 
        time = "No time data";
        System.out.println("Coldest hour in the day was " 
                            + lowest.get("TemperatureF") 
                            + "F at " 
                            + time);
    }
    
    public void testColdestDayInFiles()
    {
        String coldestFile = coldestDayInFiles();
        FileResource fr = new FileResource(coldestFile);
        CSVParser parser = fr.getCSVParser();
        CSVRecord lowest = coldestHourInFile(parser);
        System.out.println("Coldest day was in file " + coldestFile.substring(16));
        System.out.println("Coldest temperature on that day was " + lowest.get("TemperatureF"));
        System.out.println("All the Temperatures on the coldest day were:");
        parser = fr.getCSVParser();
        for (CSVRecord r : parser)
        {
            System.out.println(r.get("DateUTC") + ": " + r.get("TemperatureF"));
        }
    }
    
    public void testLowestHumidityInFile()
    {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        CSVRecord lowest = lowestHumidityInFile(parser);
        System.out.println("Lowest Humidity was " 
                            + lowest.get("Humidity") 
                            + " at " 
                            + lowest.get("DateUTC"));
    }
    
    public void testLowestHumidityInFiles()
    {
        CSVRecord lowest = lowestHumidityInFiles();
        System.out.println("Lowest Humidity was " 
                            + lowest.get("Humidity") 
                            + " at " 
                            + lowest.get("DateUTC"));
    }
    
    public void testAverageTemperatureInFile()
    {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        double avgTemp = averageTemperatureInFile(parser);
        System.out.println("Average temperature in file is " + avgTemp);
    }
    
    public void testAverageTemperatureWithHighHumidityInFile()
    {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        double avgTemp = averageTemperatureWithHighHumidityInFile(parser, 80);
        if (avgTemp == 0.0)
            System.out.println("No temperatures with that humidity");
        else
            System.out.println("Average Temp when high Humidity is " + avgTemp);
    }
    
    public static void main(String[] args)
    {
        ParsingWeatherData pwd1 = new ParsingWeatherData();
        Scanner scn = new Scanner(System.in);
        while (true)
        {
            System.out.println();
            System.out.println("What do you want to do? Type 1, 2, 3, 4, 5, 6 or 7: ");
            System.out.println("1 - Find coldest hour in a day.");
            System.out.println("2 - Find coldest hour in many days.");
            System.out.println("3 - Find lowest humidity in a day.");
            System.out.println("4 - Find lowest humidity in many days.");
            System.out.println("5 - Find average temperature in a day.");
            System.out.println("6 - Find average temperature with high humidity in a day.");
            System.out.println("7 - Stop.");
            String choice = scn.nextLine();
            if (choice.equals("7")) break;
            if (choice.equals("1")) pwd1.testColdestHourInFile();
            else if (choice.equals("2")) pwd1.testColdestDayInFiles();
            else if (choice.equals("3")) pwd1.testLowestHumidityInFile();
            else if (choice.equals("4")) pwd1.testLowestHumidityInFiles();
            else if (choice.equals("5")) pwd1.testAverageTemperatureInFile();
            else if (choice.equals("6")) pwd1.testAverageTemperatureWithHighHumidityInFile();
            else System.out.println("Invalid choice. Please enter a number from 1 to 7.");
        }
        scn.close();
    }
}
