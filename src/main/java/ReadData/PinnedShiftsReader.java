/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.ReadData;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import main.java.RunData.ShiftList;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 *
 * @author pi
 */
public class PinnedShiftsReader {

    

    static List<PinnedShift> pinnedShifts = new ArrayList<>();
    
    public PinnedShiftsReader(String filename){
        try {
            Builder builder = new Builder();
            //File file = new File(filename);
            Document doc = builder.build(getClass().getResourceAsStream(filename));
            
            Element root = doc.getRootElement();
            
            Elements elementDates = root.getChildElements("Date");
            for (Element date: elementDates) {
                
            
            LocalDate theDate = ReaderHelper.getDate(date);
            Elements shifts = date.getChildElements("Shift");
            for (Element shift: shifts) {
                String shiftType = shift.getFirstChildElement("Type").getValue();
                String consultantName = shift.getFirstChildElement("Consultant").getValue();
                pinnedShifts.add(new PinnedShift(consultantName,theDate,shiftType));
            }
            }
        } catch (ParsingException | IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    public static List<PinnedShift> getPinnedShifts() {
        return pinnedShifts;
    }
    public static void main(String[] args) {
               new DatesReader("/main/resources/Data/Dates.xml");
        new ShiftStructureReader("/main/resources/Data/Shift_Structure.xml");
        new ConsultantList("/main/resources/Data/All_Consultants.xml");
        new PinnedShiftsReader("/main/resources/Data/Pinned_Shifts.xml");
        new ShiftList();
    }
    
}
