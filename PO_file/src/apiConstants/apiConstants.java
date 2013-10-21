package apiConstants;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public abstract class apiConstants {

	public static  final String  SERVER_NAME  = "c3pudlsapp2"; // orig c3pudmapp2
	public static  final int     SERVER_PORT  = 2229;
	public static  final boolean ENCRYPTION   = false;
	public static  final String  CLIENT_CODE  = "";
	public static  final String  APPLICATION  = "PO_GUI"; //orig SupplierXRef
	public static 		 String  DSA_NAME     = "PO_file";
	public static 		 String  DESCRIPTION  = "";
	public static        String  JFILE_PATH   = "//corp03fs03/group/SilverCreek/po";
	public static        String  INFILE_NAME  = "";
	public static  		 String  OUTFILE_NAME = "";
	public static        File    INPUT_FILE   = new File(INFILE_NAME);
	public static        File    OUTPUT_FILE  = new File(OUTFILE_NAME);
	public static        ArrayList<String> JOBS_RUN    = new ArrayList<String>();
	public static		 JTextField INFILE_FIELD       = new JTextField();
	public static		 JTextField OUTFILE_FIELD      = new JTextField();
	public static		 JTextField DESCRIPTION_FIELD  = new JTextField();
	public static		 JPanel display = new JPanel();
	public static		 JTextArea CURRENT_RUN = new JTextArea();
	public static		 JTextArea PREVIOUSLY_RUN = new JTextArea();

}
