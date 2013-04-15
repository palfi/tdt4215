import java.io.BufferedReader;
import java.io.FileReader;

public class CaseReader {
	
	public static String readCase(String caseName){
		String caseText = "";
		String line = "";
		try {
			BufferedReader bReader = new BufferedReader(new FileReader("Cases/" + caseName + ".txt"));
			do {
				caseText += line;
				line = bReader.readLine();
			} while (line != null);
			bReader.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return caseText;
		
	}

}
