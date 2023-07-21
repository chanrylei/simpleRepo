package com.citi.cip.service;

import com.citi.cip.po.Holiday;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.util.CollectionUtils;

@Service
public class HolidayService {
    private static Map<String, Holiday> holidayMap = new HashMap<>();
    //从csv文件里获取数据
    public Map getHolidayMap()throws Exception{
       //csvReader 读取文件内容

        if(CollectionUtils.isEmpty(holidayMap)) {
            String userDir = System.getProperty("user.dir");
            String filePath = userDir + "\\src\\main\\resources\\holiday.csv";
            System.out.println("=========" + userDir);
            readByCsvReader(filePath);
        }
        return holidayMap;


    }
    //get holiday by country code and date
    public List<Holiday> getHolidayByDate(String date) throws Exception{
        List<Holiday> holidays = new ArrayList<>();
        Map<String, Holiday> holidayMap = getHolidayMap();
        for(Map.Entry<String, Holiday> entry: holidayMap.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            if(entry.getKey().contains(date)){
                holidays.add(entry.getValue());
            }
        }

        sortHoliday(holidays);
        return holidays;
    }
    public List<Holiday> getNextHoliday(String countryCode) throws Exception{

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DATE);
        String date = String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day);

        List<Holiday> holidays = new ArrayList<>();
        Map<String, Holiday> holidayMap = getHolidayMap();
        //比较两个日期字符串大小


        for(Map.Entry<String, Holiday> entry: holidayMap.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue().getHolidayDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(entry.getValue().getHolidayDate());
            System.out.println("=====compare date11====="+(date.compareTo(entry.getValue().getHolidayDate()) < 0));
            if (entry.getKey().contains(countryCode) && new Date().compareTo(date1) < 0) {
                holidays.add(entry.getValue());
            }
        }
        System.out.println("=====holidays====="+holidays.size());


        sortHoliday(holidays);
        return holidays;
    }

    public List<Holiday> getNextYearHoliday(String countryCode) throws Exception{
        //获取当前年
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String nextYear = String.valueOf(year+1);

        List<Holiday> holidays = new ArrayList<>();
        Map<String, Holiday> holidayMap = getHolidayMap();
        for(Map.Entry<String, Holiday> entry: holidayMap.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
            if(entry.getKey().contains(countryCode) && entry.getValue().getHolidayDate().contains(""+nextYear)){
                holidays.add(entry.getValue());
            }
        }
        sortHoliday(holidays);
        return holidays;
    }
    // use csvReader to read data from cvs file
    // return a list of Holiday

    public  static void readByCsvReader(String filePath) throws Exception {

        CsvReader csvReader = new CsvReader(filePath, ',', Charset.defaultCharset());
        csvReader.readHeaders(); //读取表头
        while(csvReader.readRecord()){
            //读取一行数据
            String[] vs = csvReader.getValues();
            for (int i=0; i<vs.length; i++) {
               Holiday holiday = new Holiday();
               holiday.setCountryCode(vs[0]);
               holiday.setContryDesc(vs[1]);
                holiday.setHolidayDate(vs[2]);
                holiday.setHolidayName(vs[3]);
                holidayMap.put(vs[0]+(vs[2].replace("'","")),holiday);
            }
        }
        System.out.println("=====readByCsvReader====="+holidayMap.size());
        csvReader.close();
    }
    //add/update new holidays
    public void updateHoliday(List<Holiday> holidays) throws Exception{
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + "\\src\\main\\resources\\holiday.csv";
        for(int i=0; i<holidays.size(); i++){
           //verify if holiday is exist
            if(holidayMap.get(holidays.get(i).getCountryCode()+holidays.get(i).getHolidayDate())!=null){
                holidayMap.put(holidays.get(i).getCountryCode()+holidays.get(i).getHolidayDate(),holidays.get(i));
            } else {
                System.out.println("====not exist====add it");
                holidayMap.put(holidays.get(i).getCountryCode()+holidays.get(i).getHolidayDate(),holidays.get(i));
            }
        }

        CsvWriter csvWriter = new CsvWriter(filePath,',', Charset.defaultCharset());
        //csvWriter 设置单元格格式为文本格式
        csvWriter.setForceQualifier(true);
        csvWriter.writeRecord(new String[]{"countryCode","countryDesc","holidayDate","holidayName"});
        //foreach holidayMap
        Iterator<Map.Entry<String, Holiday>> it = holidayMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Holiday> entry = it.next();
            csvWriter.writeRecord(new String[]{entry.getValue().getCountryCode(),entry.getValue().getCountryDesc(),"\'"+entry.getValue().getHolidayDate(),entry.getValue().getHolidayName()});
       System.out.println("====write1111===="+entry.getValue().getCountryCode()+entry.getValue().getCountryDesc()+entry.getValue().getHolidayDate()+entry.getValue().getHolidayName());
        }

        csvWriter.close();
    }

    public void removeHoliday(List<Holiday> holidays) throws Exception{
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + "\\src\\main\\resources\\holiday.csv";
        for(int i=0; i<holidays.size(); i++){
            //verify if holiday is exist
            if(holidayMap.get(holidays.get(i).getCountryCode()+holidays.get(i).getHolidayDate())!=null){
                holidayMap.remove(holidays.get(i).getCountryCode()+holidays.get(i).getHolidayDate());
            }
        }
        System.out.println("====remove===="+holidayMap.size());
        CsvWriter csvWriter = new CsvWriter(filePath,',', Charset.defaultCharset());
        //csvWriter 设置单元格格式为文本格式
        csvWriter.setForceQualifier(true);
        csvWriter.writeRecord(new String[]{"countryCode","countryDesc","holidayDate","holidayName"});
        //foreach holidayMap
        Iterator<Map.Entry<String, Holiday>> it = holidayMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Holiday> entry = it.next();
            csvWriter.writeRecord(new String[]{entry.getValue().getCountryCode(),entry.getValue().getCountryDesc(),"\'"+entry.getValue().getHolidayDate(),entry.getValue().getHolidayName()});
        }

        csvWriter.close();
    }

    //main method
    public static void main(String[] args) throws Exception {

    //    readByCsvReader("../holiday.csv");
        //获取当前类的路径
        String userDir = System.getProperty("user.dir");
        System.out.println("========="+userDir);
        HolidayService holidayService = new HolidayService();
         Map<String, Holiday>  map = holidayService.getHolidayMap();
         System.out.println("========="+map.size());
         //foreach map
        for (Map.Entry<String, Holiday> entry : map.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue().getHolidayName());
        }


      //  holidayService.addHoliday(new Holiday("CN1","China","2023-09-01","1"));
    }

    //sort the holiday list by date
    public void sortHoliday(List<Holiday> holidays) {
        //sort list
        Collections.sort(holidays, new Comparator<Holiday>() {
            @Override
            public int compare(Holiday o1, Holiday o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date1 = sdf.parse(o1.getHolidayDate());
                    Date date2 = sdf.parse(o2.getHolidayDate());
                    return date1.compareTo(date2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

}
