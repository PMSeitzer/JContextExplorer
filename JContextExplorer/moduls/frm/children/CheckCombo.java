package moduls.frm.children;

import java.awt.*;  
import java.awt.event.*;  
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.*;  
   
public class CheckCombo extends Component implements ActionListener  
{  
	
	//Fields
	private String[] ids;
	private Boolean[] values;
	private CheckComboStore[] StoredValues;
	private JComboBox combo;
	
	//Constructor
	public CheckCombo(String[] ids){
		//create components
		//import motif names
		this.ids = ids;
		
		//default: set all motifs to unselected
		values = new Boolean[ids.length];
		Arrays.fill(values, false);
		
		//initialize stored values array
		StoredValues = new CheckComboStore[ids.length];
		
		//create stored data for every array
        for(int j = 0; j < ids.length; j++)  
            StoredValues[j] = new CheckComboStore(ids[j], values[j]); 
        
        combo = new JComboBox(StoredValues);  
        combo.setRenderer(new CheckComboRenderer());  
        combo.addActionListener(this);  
	}
	
    public JPanel getContent()  
    {  
        JPanel panel = new JPanel();  
        panel.setLayout(new BorderLayout());
        panel.add(combo, BorderLayout.NORTH);  
        return panel;  
    }  
	
    public void actionPerformed(ActionEvent e)  
    {  
        JComboBox cb = (JComboBox)e.getSource();  
        CheckComboStore store = (CheckComboStore)cb.getSelectedItem();  
        CheckComboRenderer ccr = (CheckComboRenderer)cb.getRenderer();  
        ccr.checkBox.setSelected((store.state = !store.state));  
    }  

	public Boolean[] getValues() {
		return values;
	}


	public void setValues(Boolean[] values) {
		this.values = values;
	}


	public String[] getIds() {
		return ids;
	}


	public void setIds(String[] ids) {
		this.ids = ids;
	}


	public CheckComboStore[] getStoredValues() {
		return StoredValues;
	}


	public void setStoredValues(CheckComboStore[] storedValues) {
		StoredValues = storedValues;
	}  
	
	public LinkedList<String> getSelectedMotifs(){
		LinkedList<String> SelectedMotifs = new LinkedList<String>();
		
		for (CheckComboStore CCS : StoredValues){
			if (CCS.state){
				SelectedMotifs.add(CCS.id);
			}
		}
		return SelectedMotifs;
	}
}  
   
/** adapted from comment section of ListCellRenderer api */  
class CheckComboRenderer implements ListCellRenderer  
{  
    JCheckBox checkBox;  
   
    public CheckComboRenderer()  
    {  
        checkBox = new JCheckBox();  
    }  
    public Component getListCellRendererComponent(JList list,  
                                                  Object value,  
                                                  int index,  
                                                  boolean isSelected,  
                                                  boolean cellHasFocus)  
    {  
        CheckComboStore store = (CheckComboStore)value;  
        checkBox.setText(store.id);  
        checkBox.setSelected(((Boolean)store.state).booleanValue());  
        checkBox.setBackground(isSelected ? Color.BLUE : Color.LIGHT_GRAY);  
        checkBox.setForeground(isSelected ? Color.LIGHT_GRAY : Color.BLACK);  
        return checkBox;  
    }  
}  
   
class CheckComboStore  
{  
    String id;  
    Boolean state;  
   
    public CheckComboStore(String id, Boolean state)  
    {  
        this.id = id;  
        this.state = state;  
    }  
}  