/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.DisplaySolution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import main.java.DisplayData.SetLookAndFeel;
import main.java.ReadData.ConsultantList;
import main.java.ReadData.ConsultantReader;
import main.java.ReadData.DatesReader;
import main.java.RunData.RunRota;
import main.java.RunData.Shift;
import main.java.RunData.ShiftList;
import main.java.WriteData.ExportSolution;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

/**
 *
 * @author pi
 */
public class WorkingSolution extends JFrame{
    MultiKeyMap keyMap;
    List<LocalDate> dateRange;
    JPanel buttons;
    JLabel score;
    JPanel centerPanel;
    JScrollPane scroller; 
    Solver<ShiftList> solver;
    
    public WorkingSolution ()  {
        super("Processing Problem");
        SetLookAndFeel.setLookAndFeel();
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        
        // North Panel code
        JPanel northPanel = new JPanel(new BorderLayout());
        //Title
        JPanel pagetitle = new JPanel();
        JLabel pageTitle = new JLabel("Solving Problem");
        pagetitle.add(pageTitle);        
        
        //Start Box
        buttons = new JPanel();
        score = new JLabel("Score: " );
        JButton start = new JButton("Stop");
        start.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                solver.terminateEarly();
            }
        });
       buttons.add(score);
        buttons.add(start);
        
        northPanel.add(pagetitle,BorderLayout.CENTER);
        northPanel.add(buttons, BorderLayout.EAST);
        pane.add(northPanel,BorderLayout.NORTH);
                
        //West Panel code
        int x = ConsultantList.getConsultantList().size()+1;
        JPanel westPanel = new JPanel(new GridLayout(x,1));
        JPanel consultantTitle = new JPanel();
        JLabel consultanttitle = new JLabel("Consultants");
        consultantTitle.add(consultanttitle);
        westPanel.add(consultantTitle);
        for (ConsultantReader c: ConsultantList.getConsultantList()) {
            JPanel consultant = new JPanel();
            JLabel consultAnt = new JLabel(c.toString()); 
            consultant.add(consultAnt);
            westPanel.add(consultant);                    
        }
        westPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        pane.add(westPanel,BorderLayout.WEST);

        //Center Panel code
        
       // Period period = Period.between(DatesReader.getRange()[0], DatesReader.getRange()[1]);
       int period = (int) ChronoUnit.DAYS.between(DatesReader.getRange()[0], DatesReader.getRange()[1]);
        centerPanel = new JPanel(new GridLayout(x,period));
        List<JLabel> label = new ArrayList<>();
        
        LocalDate date = DatesReader.getRange()[0];
        System.out.println(period);
        
        dateRange = new ArrayList<>();
        for (int z=0; z<=period; z++) {
            JPanel tester = new JPanel();
            tester.setLayout(new BoxLayout(tester,BoxLayout.Y_AXIS));
            JLabel test = new JLabel(date.toString());
            JLabel month = new JLabel(date.getMonth().toString());
            JLabel week = new JLabel(date.getDayOfWeek().toString());
            test.setAlignmentX(CENTER_ALIGNMENT);
            month.setAlignmentX(CENTER_ALIGNMENT);
            week.setAlignmentX(CENTER_ALIGNMENT);
            
            tester.add(week);
            tester.add(month);
            tester.add(test);
            label.add(test);
            
            centerPanel.add(tester);
            dateRange.add(date);
            date = date.plusDays(1);
        }
       
        keyMap = new MultiKeyMap<>();
        
        for (ConsultantReader c : ConsultantList.getConsultantList()){
            for (LocalDate d : dateRange) {
              //  JPanel panel = new JPanel();
                ConsultantPanel consultantPane = new ConsultantPanel();
              //  panel.add(consultantPane);
                if (d.getDayOfWeek().getValue()>5){
                    consultantPane.setBackground(Color.darkGray);
                    
                }
                if (DatesReader.getBankHolidays().contains(d)) {
                    consultantPane.setBackground(Color.cyan);
                }
                keyMap.put(c,d, consultantPane);
                centerPanel.add(consultantPane);
                
            }
        }
        scroller = new JScrollPane(centerPanel, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_ALWAYS);
     
        
        pane.add(scroller,BorderLayout.CENTER);



        
        add(pane);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        SolverFactory<ShiftList> solverFactory;
        try {
            solverFactory =  SolverFactory.createFromXmlInputStream(new FileInputStream("Resources/Configuration/config.xml"));
            solver = solverFactory.buildSolver();
            runSolver();
            ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new DatesReader("/main/resources/Data/Dates.xml");
        new ConsultantList("/main/resources/Data/All_Consultants.xml");
        new WorkingSolution();
    }

    
    public void runSolver() {
        new RunRota(this);
    }

    public Solver<ShiftList> getSolver() {
        return solver;
    }
    
    
    
    public void update(ShiftScore s) {
        
        for (Object entry: keyMap.values()) {
            ConsultantPanel p = (ConsultantPanel) entry;
            p.getCOW().setVisible(false);
            p.getNOW().setVisible(false);
            p.getPaed().setVisible(false);
            p.getNeo().setVisible(false);
        }
        repaint();
        /*
        for (Map.Entry<MultiKey<?>,ConsultantPanel> entry :  keyMap.entrySet()){
            entry.getValue().getCOW().setVisible(false);
                        entry.getValue().getNOW().setVisible(false);
                                    entry.getValue().getPaed().setVisible(false);
                                    entry.getValue().getNeo().setVisible(false);


            
        }*/
  
        
        
        for (Shift shift : s.getSolution().getShiftList()) {
            ConsultantPanel pane = (ConsultantPanel) keyMap.get(shift.getConsultant(), shift.getStartDate());
         if (pane != null) {
            if (shift.getShiftType().equalsIgnoreCase("COW")) {
                pane.getCOW().setVisible(true);
            } 
            
              if (shift.getShiftType().equalsIgnoreCase("NOW")) {
                pane.getNOW().setVisible(true);
            } 
            
                if (shift.getShiftType().equalsIgnoreCase("PaedOnCall")) {
                pane.getPaed().setVisible(true);
            }  
                  if (shift.getShiftType().equalsIgnoreCase("NeoOnCall")) {
                pane.getNeo().setVisible(true);
            } 
        }}
        score.setText("Score: " + s.getScore().toShortString());
    }
    
    public void heatMap(Shift s, String string) {
                 ConsultantPanel pane = (ConsultantPanel) keyMap.get(s.getConsultant(), s.getStartDate());
           if (pane != null) {      
            myMouseAdapter mouseAdapter = new myMouseAdapter(pane,string);   
            if (s.getShiftType().equalsIgnoreCase("COW")) {
                pane.getCOW().setBackground(Color.RED);
                pane.getCOW().addMouseListener(new MouseAdapter() {
                    Popup p;
            

                    @Override
                    public void mouseEntered(MouseEvent e) {
                                      p = PopupFactory.getSharedInstance().getPopup(pane, new JLabel(string), pane.getLocationOnScreen().x, pane.getLocationOnScreen().y-50);

               p.show();
                        
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        p.hide();
                    }
                });
            } 
            
              if (s.getShiftType().equalsIgnoreCase("NOW")) {
                pane.getNOW().setBackground(Color.RED);
                pane.getNOW().addMouseListener(mouseAdapter);
            } 
            
                if (s.getShiftType().equalsIgnoreCase("PaedOnCall")) {
                pane.getPaed().setBackground(Color.RED);
                pane.getPaed().addMouseListener(mouseAdapter);
            }  
                  if (s.getShiftType().equalsIgnoreCase("NeoOnCall")) {
                pane.getNeo().setBackground(Color.RED);
                pane.getNeo().addMouseListener(mouseAdapter);
            } 
               //  pane.setBackground(Color.RED);
           }
    }
    
    public void endButtons(ShiftList solvedSolution, Map<Object, Indictment> indictment) {
        buttons.removeAll();
                  JButton export = new JButton("Export");
           JButton summary = new JButton("Staff Summary");
           JButton exit = new JButton("Exit");
           export.addActionListener(new ActionListener(){
               @Override
        public void actionPerformed(ActionEvent e) {
           
            new ExportSolution(solvedSolution, indictment);      
           }
    });
           summary.addActionListener(new ActionListener(){
               @Override
        public void actionPerformed(ActionEvent e) {
            new Summary(solvedSolution);
           }
    });
           exit.addActionListener(new ActionListener(){
               @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
 System.exit(-1);              
           }
    });
           buttons.add(score);
           buttons.add(export);
           buttons.add(summary);
           buttons.add(exit);
           revalidate();
           repaint();
              
    }
    class myMouseAdapter extends MouseAdapter {
                Popup p;
                ConsultantPanel pane;
                String string;
                public myMouseAdapter(ConsultantPanel pane, String string) {
                    this.pane = pane;
                    this.string = string;
                }
                    
                    
                    @Override
                    public void mouseEntered(MouseEvent e) {
                                      p = PopupFactory.getSharedInstance().getPopup(pane, new JLabel(string), pane.getLocationOnScreen().x, pane.getLocationOnScreen().y-50);

               p.show();
                        
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        p.hide();
                    }
           }

    
}
