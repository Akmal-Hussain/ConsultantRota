/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.WriteData;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.ReadData.DatesReader;
import main.java.RunData.Shift;
import main.java.RunData.ShiftList;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.score.constraint.Indictment;

/**
 *
 * @author pi
 */
public class ExportSolution {
    
    //Create blank workbook
      XSSFWorkbook workbook = new XSSFWorkbook();
      
      //Create a blank sheet
      XSSFSheet spreadsheet = workbook.createSheet( "Suggested Solution");

      //Create row object
      XSSFRow row;
      
    
    
    public ExportSolution(ShiftList solved, Map<Object, Indictment> indictmentMap){
        row = spreadsheet.createRow(0);
        row.createCell(0).setCellValue("Date");
        row.createCell(1).setCellValue("COW");
        row.createCell(2).setCellValue("Paed On Call");
        row.createCell(3).setCellValue("NOW");
        row.createCell(4).setCellValue("Neo On Call");
        
        Map <LocalDate, List<Shift>> map = new HashMap<> ();
               int period = (int) ChronoUnit.DAYS.between(DatesReader.getRange()[0], DatesReader.getRange()[1]);
LocalDate date = DatesReader.getRange()[0];
                for (int z=0; z<=period; z++) {
                    List<Shift> dateShifts =  new ArrayList<>(); 
                    for (Shift shift: solved.getShiftList()) {
                        if (shift.getStartDate().equals(date)) {
                            dateShifts.add(shift);
                        }
        }
                    map.put(date, dateShifts);
                    date = date.plusDays(1);
                    
                }

date = DatesReader.getRange()[0];
                  for (int z=1; z<=period+1; z++) {
XSSFCellStyle style1 = workbook.createCellStyle();
style1.setFillForegroundColor(new XSSFColor(Color.RED));
style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            row = spreadsheet.createRow(z);
            row.createCell(0).setCellValue(date.toString());
          List<Shift> shiftList = map.get(date);
            for (Shift s: shiftList) {
                int x =0;
                switch (s.getShiftType().trim()) {
                    case "COW":
                        x =1;
                        break;
                    case "NOW":
                        x=3;
                        break;
                    case "PaedOnCall":
                        x=2;
                        break;
                    case "NeoOnCall":
                        x=4;
                        break;
                }
                if (x>0 && s.getConsultant() != null) {
                row.createCell(x).setCellValue(s.getConsultant().toString());
                if (indictmentMap.get(s) != null) {
                    
                    row.getCell(x).setCellStyle(style1);
                }
                }
                                    

            }
         date = date.plusDays(1);
            
        }
        //Write the workbook in file system
        try {
            FileOutputStream out = new FileOutputStream(
                    new File("src/main/resources/Solutions/ExportSolution.xlsx"));
            
            workbook.write(out);
            out.close();
            System.out.println("ExportSolution.xlsx written successfully");
        } catch (IOException iOException) {
            System.out.println("Writing unsuccessful");
        }
        
    }
}
