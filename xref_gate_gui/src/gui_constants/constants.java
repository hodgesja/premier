package gui_constants;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JTextField;



public abstract class constants {

	public static ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	public static ArrayList<String> checkStrings = new ArrayList<String>();
	
	public static ArrayList<String> reprocess = new ArrayList<String>();
	
	public static int maxLeng = 0;
	public static int numOfTabs = 3;
	public static String outFileQa        = "//corp03fs03/group/datamgmt/silver creek/ab_initio_share/supplier_xref/QA/supplier_xref_category.csv";
	public static String outFileProd      = "//corp03fs03/group/datamgmt/silver creek/ab_initio_share/supplier_xref/PROD/supplier_xref_category.csv";
	public static String backOutFileQa    = "//corp03fs03/group/datamgmt/silver creek/ab_initio_share/supplier_xref/QA/supplier_xref_backout.csv";
	public static String backOutFileProd  = "//corp03fs03/group/datamgmt/silver creek/ab_initio_share/supplier_xref/PROD/supplier_xref_backout.csv";
	
	public static String wavePath = "//corp03fs03/group/Datamgmt/Silver Creek/ab_initio_share/supplier_xref/category_gui";
	
	public static String dbConnect = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = c3puoraext1)(PORT = 1523))(ADDRESS = (PROTOCOL = TCP)" +
			"(HOST = c3puoraext1)(PORT = 1521))(CONNECT_DATA = (SERVER = DEDICATED)(SERVICE_NAME = scaxep)))";
	
	public static JTextField errors = new JTextField();
	
	public static String userName = "passport_etl";
	public static String password = "etlu5er";
	public static String query    = "Select distinct CATEGORY from XE_READ.NS_CROSS_REF_TABLE";
	
	
	public static boolean prod = false;
	public static boolean gate = true;
}
