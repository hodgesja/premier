package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;

import gui_constants.constants;

/**
 * 
 * @author       jhodges1
 * @version      1.0
 * description:  Java Gui that allows the users to choose what categories need to be (re)grouped 
 * 					once categories have been chosen, creates or modifies a .txt file in csv format
 * 					with the selected categories.
 *
 */
public class gui extends JFrame implements ActionListener {
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//create gui
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui ex = new gui();
                ex.setSize(1000,400);
                ex.setTitle("QA Category to Group");
                ex.setLocationRelativeTo(null);
                ex.pack();
                ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ex.setVisible(true);
            }
        });
	}
	
	/**
	 * description: creates a new tab for each wave and adds each category in that wave
	 * 					as a checkbox. Also provides a 'Complete' button that updates the
	 * 					txt file
	 */
	public gui(){
		
		//Create arrayLists for the checkboxes and strings to name each check box 
		ArrayList <String> categories = connectDatabase();
		ArrayList <JCheckBox> checkCategory = getCheckedBoxes(categories);
		
		//Check the boxes that are already slated for regrouping
		checkCategory = priorChecks(checkCategory, categories);
		
		//Set the 'constant' arrayLists
		constants.checkStrings = categories;
		constants.checkBoxes   = checkCategory;
		
		//Create the tabbed view
		JTabbedPane tabbedPane = new JTabbedPane();
			
		//Create the base of the GUI
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		getContentPane().add(topPanel);
		
		//Fill each tab with the checkboxes
		int i = 0;
		for(i=0; i < constants.numOfTabs; i++){
			JPanel panel0 = populateTab(checkCategory, i);
			tabbedPane.addTab( "Page " + (i+1), panel0 );
		}
		
		//Create and add the radioButtons
		JPanel qaPanel = new JPanel(new GridLayout(1,2));
		JRadioButton gate   = new JRadioButton("Open Gate");
		JRadioButton backout = new JRadioButton("Back Out");
		
		gate.setSelected(true);
	
		gate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				constants.gate = true;
				uncheckAll();
			}
		});
		
		backout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				constants.gate = false;
				uncheckAll();
			}
		});
		
		//Create the Button groups
		ButtonGroup group2 = new ButtonGroup();
		qaPanel.add(gate);
		qaPanel.add(backout);
		
	    //Add the radioButtons to the GUI
		group2.add(gate);
		group2.add(backout);
		
	    //add the three portions of the GUI
		topPanel.add( tabbedPane, BorderLayout.CENTER );
		topPanel.add( qaPanel,  BorderLayout.NORTH);
		topPanel.add( constants.errors,  BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 * @param category
	 * @param tabNum
	 * @return JPannel
	 * 
	 * Description:   Populates the tab with a checkbox for each category
	 */
	public JPanel populateTab(ArrayList <JCheckBox> category, double tabNum){
		
		//Create the JPanel that will be returned
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		Box vertical_a = Box.createVerticalBox();

		//Divide number of checkboxes into the correct number of tabs
		double lowerBound = category.size() * (tabNum/constants.numOfTabs);
		double upperBound = category.size() * ((tabNum+1)/constants.numOfTabs);
		int i = 0;
		
		//populate each tab with the appropriate number of boxes
		for (i = (int)lowerBound; i < ((int)upperBound); i++){
			vertical_a.add(category.get(i));
		}
		
		//Add the checkboxes to the GUI
		panel.add(vertical_a, BorderLayout.CENTER);

		//add button to each tab
		JButton done = new JButton("Complete");
		done.addActionListener(this);
		panel.add(done, BorderLayout.SOUTH);
	
		return panel;
	}

	/**
	 * Simple method that clears all of the checkboxes and then re-checks them
	 * 		based on which radioButton was just choosen
	 * 		Triggered each time that a radio button is clicked
	 */
	public void uncheckAll(){
		int i = 0;
		
		//Uncheck all checkboxes
		for(i=0; i<constants.checkBoxes.size();i++){
			constants.checkBoxes.get(i).setSelected(false);
		}
		
		priorChecks(constants.checkBoxes, constants.checkStrings);
		
		//Calls the method to recheck appropriate
		if(constants.gate == false){
			System.out.println("Calling backout radio");
		}
	}
	
	@Override
	/**
	 * Listener for the Complete' button
	 * Updates the appropriate .txt file with categories to be reprocessed.
	 */
	public void actionPerformed(ActionEvent e) {
		
		int i = 0;
		constants.reprocess.clear();
		
		while(i < constants.checkBoxes.size()){
			if(constants.checkBoxes.get(i).isSelected()){
				constants.reprocess.add(constants.checkStrings.get(i));
			}
			i++;
		}
		
		if(constants.gate == false){
			System.out.println("Calling backout");
			
			//update the file and give a verification message
			if(writeBackout(constants.reprocess) != false){
				JOptionPane.showConfirmDialog(gui.this, "Completed. Backout file updated successfully", "Backout Complete", JOptionPane.CLOSED_OPTION);
			}else{
				JOptionPane.showMessageDialog(gui.this, "Error updating backout file, could not complete update.\n\n The file may be open within another application.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}else{

			//If the checkBox was checked, then add it to the arrayList for re-processing
		
	
			//update the file and give a verification message
			if(writeGate(constants.reprocess) != false){
				JOptionPane.showConfirmDialog(gui.this, "Completed. File updated successfully", "Open Gate Complete", JOptionPane.CLOSED_OPTION);
			}else{
				JOptionPane.showMessageDialog(gui.this, "Error updating file, could not complete update.\n\n The file may be open within another application.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * 
	 * @param regroup
	 * @return boolean
	 * 
	 * Description: Updates the file with the selected categories
	 */
	public boolean writeGate(ArrayList<String> regroup){
		
		int i = 0;
		//Define the new line char for .csv file
		String newLine = System.getProperty("line.separator");
		
		//add all categories that are to be re-processed to the same arrayList
		for(i=0; i < regroup.size(); i++){
			constants.reprocess.add(regroup.get(i));
			regroup.remove(i);
		}
		
		//Call this method to remove any duplicate categories
		dupes();
		
		//Put the categories in alphabetical order
		Collections.sort((constants.reprocess.subList(0, constants.reprocess.size())));
		
		//Default output file is the qa file
		File file = new File(constants.outFileQa);
		
		//Determine the output file based on the radioButton selection

			file = new File(constants.outFileQa);
		

		/**
		//Clear the excell csv file
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		*/
		
		//Write to the file
		try {
			
			//Create objects needed to write to the file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String totalString = "";
		
			//Concatenate all of the category names together in a singleString with newline chars
			for(i=0; i < constants.reprocess.size(); i++){
				totalString += constants.reprocess.get(i) + newLine;
			}
			
			//Write to file and close the Writers
			bw.write(totalString);
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean writeBackout(ArrayList<String> regroup){
		int i = 0;
		String total = "";
		String newLine = System.getProperty("line.separator");
		
		for(i=0; i < regroup.size(); i++){
			total = total + regroup.get(i) + newLine;
			//System.out.println(regroup.get(i) + "," + newLine);
		}
		System.out.println(total);
		
		File file = new File(constants.backOutFileQa);
		
		//Determine the output file based on the radioButton selection

			file = new File(constants.backOutFileQa);
		
		
		
		//Write to the file
		try {
			
			//Create objects needed to write to the file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			//Write to file and close the Writers
			bw.write(total);
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		closeGate(regroup);
		return true;
	}
	
	public void closeGate(ArrayList<String> regroup){


		File file;
		int i = 0;
		int j = 0;
		

			file = new File(constants.outFileQa);
		
		System.out.println(""+ regroup.size());
		for(i=0;i<regroup.size();i++){
			System.out.println(i + "  group " +regroup.get(i) + "\n");
		}
		
		try{
			if(file != null){
				Scanner in = new Scanner(file);

				//check JCheckBox that are already in the CSV
				while(in.hasNextLine()){
					String line = in.nextLine();
					for(i=0; i < constants.checkStrings.size(); i++){	
						if(constants.checkStrings.get(i).compareToIgnoreCase(line) == 0){
							constants.reprocess.add(constants.checkStrings.get(i));
						}
					}
				}
				in.close();
			}
			
			for(i=0;i<constants.reprocess.size();i++){
				System.out.println(i + "  proc  " +constants.reprocess.get(i) + "\n");
			}
			
			
			for(i=0; i<regroup.size(); i++){
				for(j=0; j<constants.reprocess.size(); j++){
					if(regroup.get(i).equals(constants.reprocess.get(j))){
						constants.reprocess.remove(j);
						System.out.println(i + "  " + j);
					}
				}
			}
			
			
			for(i=0;i<constants.reprocess.size();i++){
				System.out.println(i + "  " +constants.reprocess.get(i) + "\n");
			}
		//writeGate(constants.reprocess);	
			
		}catch(java.io.FileNotFoundException f){

		}
		
	}
	
	/**
	 * Description: Method removes duplicates from the arrayList for reprocessing
	 */
	public void dupes(){
		  for ( int i = 0; i < constants.reprocess.size(); i++ ){
		     for ( int j = 0; j < constants.reprocess.size(); j++ ){
		        if ( i == j ){
		        	//do nothing
		        } else if ( constants.reprocess.get( j ).equals( constants.reprocess.get( i ) ) ) {
		        	constants.reprocess.remove( j );
		        }
		     }
		  }
	}
	

	
	/**
	 * Description: Returns an arrayList of JCheckBoxes for every String in the param arrayList
	 * @param category
	 * @return checks
	 */
	public ArrayList<JCheckBox> getCheckedBoxes(ArrayList<String> category){
		//initialize variables needs
		ArrayList<JCheckBox> checks = new ArrayList<JCheckBox>();
		int size = category.size();
		int i = 0;
		
		//create a new JCheckBox for each category and add it to a new arrayList of JCheckBoxes
		for(i=0; i < size; i++){
			checks.add(new JCheckBox(category.get(i)));
		}
		
		//Set the number of tabs shown in the gui
		if((category.size() > ((category.size()/15)*15))){ //handles division issues caused by integer division
			constants.numOfTabs = (category.size()/15) + 1;
		}else{
			constants.numOfTabs = (category.size()/15);
		}
		return checks;
	}
	
	/**
	 * Description: Pre-populates the checkboxes that are already scheduled to be processed
	 * 				returns the checks arraylist with previously selected jcheckboxes checked
	 * @param checks
	 * @param names
	 * @return
	 */
	public ArrayList<JCheckBox> priorChecks(ArrayList<JCheckBox> checks, ArrayList<String> names){
		
		File file;
		

			if(constants.gate != true){
				file = new File(constants.backOutFileQa);
				//System.out.println(constants.backOutFileQa);
			}else{
				file = new File(constants.outFileQa);
				//System.out.println(constants.outFileQa);
			}
		
					
		constants.reprocess.clear();
		
		try{
			if(file != null){
				Scanner in = new Scanner(file);
				int i = 0;
				//check JCheckBox that are already in the CSV
				while(in.hasNextLine()){
					String line = in.nextLine();
					for(i=0; i < names.size(); i++){	
						if(names.get(i).compareToIgnoreCase(line) == 0){
							checks.get(i).setSelected(true);
							constants.reprocess.add(names.get(i));
						}
					}
				}
				in.close();
			}
		}catch(java.io.FileNotFoundException f){
			return checks;
		}
		
		return checks;
	}
	
	/**
	 * Connect to silvercreek prod to get the list of category names
	 * @return uniqueCategories  -An arraylist of strings that contains each distinct category.
	 */
	public  ArrayList<String> connectDatabase(){
	
		 ArrayList<String> uniqueCategories = new ArrayList<String>();

	    try {
	    	Connection connection = null;
	    	//Connect to silver creek prod
	    	constants.errors.setText("disonnected");
			
	    	connection = DriverManager.getConnection(
	    			constants.dbConnect, constants.userName, constants.password);
	    	constants.errors.setText("Connected");
	    	
	    	//Create and run query
	    	Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(constants.query);
	        
	        //results from query
	        while(rs.next()){
	        	if(rs.getString("CATEGORY").length() > 75){
	        		uniqueCategories.add((rs.getString("CATEGORY")).substring(0, 72) + "...");
	        	}else{
	        		uniqueCategories.add(rs.getString("CATEGORY"));
	        	}
	        }
	        
	        Collections.sort((uniqueCategories.subList(0, uniqueCategories.size())));
	        
	    	connection.close();
						
		} catch (SQLException e) {
			constants.errors.setText("Error connecting to the database. Could not connect.");
			e.printStackTrace();
		}
	    return uniqueCategories;
	}
}
