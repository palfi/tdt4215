package patientCase;

import java.util.ArrayList;

public class PatientCase {
	private String caseName;
	private ArrayList<String> textLines;
	private String[] icdCodes;
	
	public PatientCase(String caseName, ArrayList<String> textLines) {
		this.caseName = caseName;
		this.textLines = textLines;
	}

	public ArrayList<String> getTextLines() {
		return textLines;
	}

	public void setTextLines(ArrayList<String> textLines) {
		this.textLines = textLines;
	}

	public String getcaseName() {
		return caseName;
	}

	public void setcaseName(String caseName) {
		this.caseName = caseName;
	}

	public String[] getIcdCodes() {
		return icdCodes;
	}

	public void setIcdCodes(String[] icdCodes) {
		this.icdCodes = icdCodes;
	}
	
	
}
