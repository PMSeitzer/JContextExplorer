package genomeObjects;

import javax.swing.UIManager;

import haloGUI.StartFrame;

public class JContextExplorer_old {
	
	public static void main(String[] args) throws Exception{
		
		//set UI to look natural
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		//bring up starting window
		new StartFrame("Welcome to JContextExplorer!");

	}

}