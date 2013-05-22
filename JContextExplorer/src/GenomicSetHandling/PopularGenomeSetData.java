package GenomicSetHandling;

import javax.swing.JCheckBoxMenuItem;

public class PopularGenomeSetData {

	//Fields
	private String Name;
	private JCheckBoxMenuItem chkBox;
	private String URL;
	private boolean PasswordProtected;
	private String Password;
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public boolean isPasswordProtected() {
		return PasswordProtected;
	}
	public void setPasswordProtected(boolean passwordProtected) {
		PasswordProtected = passwordProtected;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public JCheckBoxMenuItem getChkBox() {
		return chkBox;
	}
	public void setChkBox(JCheckBoxMenuItem chkBox) {
		this.chkBox = chkBox;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	
}
