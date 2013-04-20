package owl;
import java.io.Serializable;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

@SuppressWarnings("serial")
public class OWL_Class_Array implements Serializable{
	
	private ArrayList<OWL_Class> owl_classes;

	public OWL_Class_Array() {
		owl_classes = new ArrayList<OWL_Class>();
	}

	public void add(OWLOntology o, OWLClass cls) {
		owl_classes.add(OWL_Class.parseOwlClass(o, cls));
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		for (OWL_Class c : owl_classes) {
			str.append(c.toString());
			str.append('\n');
		}
		return str.toString();
	}
	
	public ArrayList<OWL_Class> getOwl_Classes(){
		return owl_classes;
	}
}
