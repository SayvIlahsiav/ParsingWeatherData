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
    public CSVRecord lowestOfTwo(CSVRecord currentRow, CSVRecord lowestSoFar)
    {
        if (lowestSoFar == null)
                lowestSoFar = currentRow;
        else
        {
            double currentTemp = Double.parseDouble(currentRow.get("TemperatureF"));
            double lowestTemp = Double.parseDouble(lowestSoFar.get("TemperatureF"));
            if(currentTemp < lowestTemp && currentTemp != -9999)
                lowestSoFar= currentRow;
        }
        return lowestSoFar;
    }
    
    public CSVRecord coldestHourInFile(CSVParser parser)
    {
        CSVRecord lowestSoFar = null;
        for (CSVRecord currentRow : parser)
        {
            lowestSoFar = lowestOfTwo(currentRow, lowestSoFar);
        }
        return lowestSoFar;
    }
    
    public String coldestDayInFiles()
    {
        CSVRecord lowestSoFar = null;
        DirectoryResource dr = new DirectoryResource();
        String fileName = null;
        for (File f : dr.selectedFiles())
        {
            fileName = f.getName();
            FileResource fr = new FileResource(f);
            CSVRecord currentRow = coldestHourInFile(fr.getCSVParser());
            lowestSoFar = lowestOfTwo(currentRow, lowestSoFar);
        }
        return fileName;
    }
    
    public void testColdestHourInFile()
    {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        CSVRecord lowest = coldestHourInFile(parser);
        System.out.println("Coldest hour in the day was " 
                            + lowest.get("TemperatureF") 
                            + "F at " 
                            + lowest.get("TimeEST"));
    }
    
    public void testColdestDayInFiles()
    {
        String coldestFile = coldestDayInFiles();
        FileResource fr = new FileResource("nc_weather/2014/" + coldestFile);
        CSVParser parser = fr.getCSVParser();
        CSVRecord lowest = coldestHourInFile(parser);
        System.out.println("Coldest day was in file " + coldestFile);
        System.out.println("Coldest temperature on that day was " + lowest.get("TemperatureF"));
        System.out.println("All the Temperatures on the coldest day were:");
        parser = fr.getCSVParser();
        for (CSVRecord r : parser)
        {
            System.out.println(r.get("DateUTC") + ": " + r.get("TemperatureF"));
        }
    }
    
    public static void main(String[] args)
    {
        ParsingWeatherData pwd1 = new ParsingWeatherData();
        //pwd1.testColdestHourInFile();
        pwd1.testColdestDayInFiles();
    }
}
