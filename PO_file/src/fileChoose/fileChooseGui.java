package fileChoose;
import apiConstants.apiConstants;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import com.onerealm.solx.api.client.WfgClient;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author jhodges1
 * @version 1.0
 * 
 * Description:		Provide a GUI that allows the user to run a DSA with a text file input. 
 * Reason:			Users would need ADMIN rights to run this from the server, since we don't want everyone to have admin rights,
 * 						this GUI uses the java API, which bypasses the need for admin rights when running the DSA PO_file
 *
 */
public class fileChooseGui extends JFrame implements ActionListener{
 
	/**
	 *  Calls the run() method to create/display GUI
	 * @param args
	 */
	public static void main(String[] args) {
		
		//create gui
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	
            	fileChooseGui ex = new fileChooseGui();
            	ex.repaint();
                ex.setSize(600,400);
                ex.setTitle("PO_file");
                ex.setLocationRelativeTo(null);
                ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ex.setVisible(true);
            	
            }
        });
	}
	
	/**
	 * Fills the GUI and allows the user to interact with it
	 * 
	 * @throws java.lang.NullPointerException
	 */
	public fileChooseGui(){
		
		//Create panels and borders
		JPanel dsa_choose = new JPanel();
		dsa_choose.setBorder(new EmptyBorder(10, 15, 10, 15));
		JPanel description = new JPanel();
		description.setBorder(new EmptyBorder(10, 15, 10, 15));
		
		getContentPane().setLayout(new GridLayout(3,1));
		getContentPane().add(dsaPanel(dsa_choose));

		//Set the layout for the top half (run button upwards) of the GUI
		apiConstants.display.setLayout(new GridLayout(5,1));
		
		//Create button objects
		JButton selectInput = new JButton("Browse For Input File");
		JButton selectOut = new JButton("Browse For Output Directory");
		JButton run = new JButton("Run DSA");
		
		//Turn off 'create new file' option on the JFileChooser
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		
		//add an action listener to selectInput button
		selectInput.addActionListener(new ActionListener(){
			//in-line action listener
			public void actionPerformed(ActionEvent e) {
				//create file a fileChooser objects
				File file = new File(apiConstants.JFILE_PATH);
				JFileChooser fileOpener = new JFileChooser();
				
				//open fileChooser, have it pointing to the share drive's silvercreek folder
				fileOpener.setCurrentDirectory(file);
				fileOpener.showOpenDialog(apiConstants.display);
				
				//file is set to the selected file
				file = fileOpener.getSelectedFile();
				
				//check to see if file is null
				//		-if it is: File path is invalid
				//		-if not  : continue with possible good file path
				if(file != null){
					String in = "\\share\\" + file.getPath().substring(19);
					in = in.replace('\\', '/');
					setInFilePath(in);
				}else{
					apiConstants.INFILE_FIELD.setText("Please choose a file");
					setInFilePath("Please choose a file");
				}
			}
		});

		//add an action listener to selectOutput button
		selectOut.addActionListener(new ActionListener(){
			//in-line action listener
			public void actionPerformed(ActionEvent e) throws java.lang.NullPointerException{
				//create file a fileChooser objects
				File file = new File(apiConstants.JFILE_PATH);
				JFileChooser fileOpener = new JFileChooser();
				
				//open fileChooser, have it pointing to the share drive's silvercreek folder
				fileOpener.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileOpener.setCurrentDirectory(file);
				fileOpener.showOpenDialog(apiConstants.display);
				//file is set to the selected file
				file = fileOpener.getSelectedFile();
				
				//check to see if file is null
				//		-if it is: File path is invalid
				//		-if not  : continue with possible good file path
				if(file != null){
					String out = "\\share\\" + file.getPath().substring(19);
				
					out = out.replace('\\', '/');
				
					setOutFilePath(out);
				}else{
					apiConstants.OUTFILE_FIELD.setText("Please choose a directory");
					setOutFilePath("Please choose a directory");
				}
			}
		});
		
		//Add the action listener to run button
		run.addActionListener(this);
	
		//Don't let users type in the feedback
		apiConstants.OUTFILE_FIELD.setEditable(false);
		apiConstants.INFILE_FIELD.setEditable(false);
		apiConstants.PREVIOUSLY_RUN.setEditable(false);
		apiConstants.CURRENT_RUN.setEditable(false);
		
		//Create panel for bottom half (feedback) of the GUI
		JPanel feedback = new JPanel(new GridLayout(1,2));
		
		//Create border around the two feedback panes
		apiConstants.CURRENT_RUN.setBorder(new EmptyBorder(10, 15, 10, 15));
		
		apiConstants.PREVIOUSLY_RUN.setBorder(new EmptyBorder(10, 15, 10, 15));

		//add the two feedback panes to bottom panel
		feedback.add(apiConstants.CURRENT_RUN);
		feedback.add(apiConstants.PREVIOUSLY_RUN);
		
		//add buttons and in/out file feedback to top half of the GUI
		apiConstants.display.add(selectInput);
		apiConstants.display.add(apiConstants.INFILE_FIELD);
		apiConstants.display.add(selectOut);
		apiConstants.display.add(apiConstants.OUTFILE_FIELD);
		apiConstants.display.add(run);
		
		apiConstants.display.setBorder(new EmptyBorder(5, 15, 5, 15));
		
		//Initial text for the bottom feedback
		apiConstants.CURRENT_RUN.setText("Please choose input and output files.");
		apiConstants.PREVIOUSLY_RUN.setText("Jobs Created This Session:");
		
		//Add the two panels (bottom two thirds) to the GUI and set thte layout
		getContentPane().add(apiConstants.display);
		getContentPane().add(feedback);
		
	}
	
	/**
	 * Creates the top third of the gui which allows users to input their orn description and choose DSA they want to run
	 * 
	 * @param in
	 * @return
	 */
	public JPanel dsaPanel(JPanel in){
		
		//Set panel layout for choosing DSA and inputing description
		in.setLayout(new GridLayout(2,2,15,15));
		
		//List to populate the drop-down
		String[] dsa_names = {
				"PO_file",
				"PO_file_alt",
				"Invc_file"
			 };
		JLabel dsa_text = new JLabel();
		JComboBox dsa_choice = new JComboBox(dsa_names);
		JLabel description_text = new JLabel();
		
		//Set Labrels
		dsa_text.setText("Choose DSA:");
		description_text.setText("Description:");
		
		//In-line action listener to set the DSA name when one is chosen from the combobox
		dsa_choice.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
			       JComboBox cb = (JComboBox)e.getSource();
			       apiConstants.DSA_NAME = (String)cb.getSelectedItem();
			}
		});
		
		//add the four pomponents to the panel
		in.add(dsa_text);
		in.add(dsa_choice);
		in.add(description_text);
		in.add(apiConstants.DESCRIPTION_FIELD);
		
		return in;
	}

	@Override
	/**
	 * Action Listener for the run button
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		//check return value
		if(apiCall(apiConstants.INFILE_NAME, apiConstants.OUTFILE_NAME)){
			System.out.println("Success");
		}else{
			System.out.println("Failure");
			apiConstants.CURRENT_RUN.setText(apiConstants.CURRENT_RUN.getText() + System.getProperty("line.separator") + "DSA API Call failed" +
					System.getProperty("line.separator") + System.getProperty("line.separator") + "Could not run DSA");
		}
	}
	
	/**
	 * Setter method for the inFile path
	 * @param filePath
	 */
	public void setInFilePath(String filePath){
		apiConstants.INFILE_NAME = filePath;
		apiConstants.INFILE_FIELD.setText(filePath);
	}
	
	/**
	 * Setter method for the outFile path
	 * @param filePath
	 */
	public void setOutFilePath(String filePath){
		apiConstants.OUTFILE_NAME = filePath;
		apiConstants.OUTFILE_FIELD.setText(filePath);
	}
	
	/**
	 * Main functionality of the program, the API call is made to the server to run the DSA
	 * 
	 * @param inFile
	 * @param outFile
	 * @return
	 */
	public boolean apiCall(String inFile, String outFile){
		
		 //Initialize client for api call
		  WfgClient wfgClient = new WfgClient(apiConstants.SERVER_NAME, apiConstants.SERVER_PORT, apiConstants.ENCRYPTION,
				 				   apiConstants.CLIENT_CODE, apiConstants.APPLICATION);
		  
		  //Set the filepath seperater
		  String line = System.getProperty("line.separator");

		  //Check if file is null and verify that it contains '/' (indicating a valid file path)
		  if(inFile.contains("/")==false || outFile.contains("/")==false || inFile == "" || outFile == ""){
			  apiConstants.CURRENT_RUN.setText("Please choose input and output files." + line + line + "Files are not valid");
			  return false;
		  }
		 
		  //Verify output file's path is in an output 
		  if(outFile.contains("out") == false){
			  apiConstants.CURRENT_RUN.setText("Please choose input and output files." + line + line + "Output directory must be within an " + line +
					  "'out' directory");
			  return false;
		  }
		  
		 try {
			 //set input and output files	 
			 apiConstants.INFILE_FIELD.setText("API call infile path: " + inFile);
			 apiConstants.OUTFILE_FIELD.setText("API call outfile path: " + outFile);
			 wfgClient.setInputFilePath(inFile);
			 wfgClient.setOutputDirectory(outFile);
			 
			 //Output goes to the right-hand feedback
			 apiConstants.CURRENT_RUN.setText("Calling DSA API..." + line);
			 
			 //Take the input file path and get the txt file name and set the description for the API call
			 if(apiConstants.DESCRIPTION_FIELD.getText().length() > 500){
				 apiConstants.DESCRIPTION = apiConstants.DESCRIPTION_FIELD.getText().substring(1, 499);
			 }else{
				 apiConstants.DESCRIPTION = apiConstants.DESCRIPTION_FIELD.getText();
			 }
			 
			//Run the job and get the jobID
		 	int m_jobID = wfgClient.runJob(apiConstants.DSA_NAME, apiConstants.DESCRIPTION);

		 	//Output goes to the right-hand feedback
		 	apiConstants.CURRENT_RUN.setText(apiConstants.CURRENT_RUN.getText() + line + "API Call Complete" + line + "Job Has Begun" + line + "Job ID = " + m_jobID);
			updatePrint(m_jobID);
		 } catch (Exception e) {
			//e.printStackTrace();	
			return false;
		 }
	//API completed successfully
	return true;
	}
	
	/**
	 * Logs the current sessions history
	 * @param jobId
	 */
	public void updatePrint(int jobId){
		
		//Job_Id as string
		String entry = "" + jobId + "\t";
		
		//Check length of the description (input file) and add file name to job_Id string
		if(apiConstants.DESCRIPTION.length() > 15){
			//if longer than 15 chars, append '...' after 15 chars
			entry = entry + apiConstants.DESCRIPTION.subSequence(0, 15) + "...";
		}else{
			//if less than 15 chars, add the whole description
			entry = entry + apiConstants.DESCRIPTION;
		}
		
		//add job_id and file name as a string to the arraylist
		apiConstants.JOBS_RUN.add(entry);
	
		int i = 0;
		String total = "Jobs Created This Session:\n";
		//create single String that contains all entries in the arrayList
		for(i=0;i<apiConstants.JOBS_RUN.size();i++){
			total = total + apiConstants.JOBS_RUN.get(apiConstants.JOBS_RUN.size() - 1 - i) + "\n";
		}
		
		//Put the text (all arraylist entries) in the right hand feedback area 
		apiConstants.PREVIOUSLY_RUN.setText(total);		
	}	
}
