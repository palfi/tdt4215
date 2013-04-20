package owlParser;

/*
 * Change default entityExpansionLimit to 120000 in run configuration
 * Run Configuration / Arguments / vm argguments = -DentityExpansionLimit=120000
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OwlParser {

	private ArrayList<OWL_Class> owl_classes;

	private void writeOwlClasses(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(owl_classes);
		oos.close();
	}

	@SuppressWarnings("unchecked")
	private void readOwlClasses(File file) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		owl_classes = (ArrayList<OWL_Class>) ois.readObject();
		ois.close();
	}

	public OwlParser() {
		owl_classes = new ArrayList<OWL_Class>();
	}

	public ArrayList<OWL_Class> getOwl_Classes() {
		return owl_classes;
	}

	public void parse(String path, String fileName)
			throws OWLOntologyCreationException, IOException {
		File out = new File(path + "temp/" + fileName + "_parsed.txt");

		if (!new File(path + "temp").exists()) {
			new File(path + "temp").mkdir();
		}

		if (!out.exists()) {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			File file = new File(path + fileName);
			OWLOntology o = manager.loadOntologyFromOntologyDocument(file);
			for (OWLClass cls : o.getClassesInSignature()) {
				owl_classes.add(OWL_Class.parseOwlClass(o, cls));
			}
			writeOwlClasses(out);
		} else {
			try {
				readOwlClasses(out);
			} catch (ClassNotFoundException e) {
				System.err.println(e.getMessage());
				out.delete();
				parse(path, fileName);
			}
		}
	}

}
