package com.citi.cip.controller;

import com.citi.cip.po.Holiday;
import com.citi.cip.service.DemoService;
import com.citi.cip.service.HolidayService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController


public class DemoController {


    @Autowired
    HolidayService holidayService;
    //写一个GetMapping
    //返回一个JSONObject
    //传入一个JSONObject

    @PostMapping("/addorupdateHoliday")
    @ResponseBody
    public JSONObject gettoken(@RequestBody List<Holiday> params){
        System.out.println("====params==="+params);

        //把jsonobject 转成list

        JSONObject json = new JSONObject();
        try {
            holidayService.updateHoliday(params);
            json.put("status","success");
        }catch (Exception e) {
        }

        return json;
    }
    @PostMapping("/removeHoliday")
    @ResponseBody
    public JSONObject removeHoliday(@RequestBody List<Holiday> params){
        System.out.println("====params==="+params);

        JSONObject json = new JSONObject();
        try {
            holidayService.removeHoliday(params);
            json.put("status","success");
        }catch (Exception e) {
        }

        return json;
    }

    @GetMapping("/getNextHoliday")
    @ResponseBody
    public JSONArray getNextHoliday(@RequestParam String countryCode){
        List<Holiday> holidays = new ArrayList<>();
        System.out.println("====countryCode==="+countryCode);
        try {
            holidays = holidayService.getNextHoliday(countryCode);
        } catch (Exception e) {
        }

        return JSONArray.fromObject(holidays);

    }
    @GetMapping("/getNextYearHoliday")
    @ResponseBody
    public JSONArray getNextYearHoliday(@RequestParam String countryCode){
        List<Holiday> holidays = new ArrayList<>();
        System.out.println("====countryCode==="+countryCode);
       try {
            holidays = holidayService.getNextYearHoliday(countryCode);
        } catch (Exception e) {
        }

        return JSONArray.fromObject(holidays);

    }
    @GetMapping("/getHolidayByDate")
    @ResponseBody
    public JSONArray getHolidayByDate(@RequestParam String holidayDate){
        System.out.println("====holidayDate==="+holidayDate);
        if(!validateDateFormat(holidayDate)) {
            System.out.println("====holidayDate1111==="+holidayDate);
            JSONObject json = new JSONObject();
            json.put("status","invalid date format");
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(json);
            return jsonArray;
        }

        List<Holiday> holidays = new ArrayList<>();
    try {
        holidays = holidayService.getHolidayByDate(holidayDate);
    } catch (Exception e) {
    }

        return JSONArray.fromObject(holidays);
    }

    //validate date format is "yyyy-mm-dd"
    public boolean validateDateFormat(String date){
        System.out.println("====datewwwww==="+date);
        boolean result = false;
        String[] dateArray = date.split("-");
        System.out.println("====dateArray==="+dateArray.length);
        if(dateArray.length == 3){
            if(dateArray[0].length() == 4 && dateArray[1].length() == 2 && dateArray[2].length() == 2){
                result = true;
            }
        }
        return result;
    }

}
