package owl;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import java.io.Serializable;

/*
 * Example of an icd10 class:
 * classId: E12
 * code_formatted: E12
 * code_compacted: E12
 * label: Underern¾ringsrelatert diabetes mellitus
 * umls_tui: T047
 * umls_conceptId: C0271641
 * umls_atomId: A0996701
 * umls_semanticType: Disease or Syndrome 
 * synonym: sukkersyke 
 * inclusion: underern¾ringsrelatert <i>diabetes mellitus</i> type II underern¾ringsrelatert <i>diabetes mellitus</i> type I 
 * exclusion: R730 E748 R81 E891 O24 P702
 * 
 * 
 * Example of an atc class:
 * classId: R41
 * label: Andre symptomer og tegn med tilknytning til kognitive funksjoner og bevissthet
 * 
 */

@SuppressWarnings("serial")
public class OWL_Class implements Serializable {

	private static final String PREFIX = "http://research.idi.ntnu.no/hilab/ehr/ontologies/icd10no.owl#";
	private static final String CODE_FORMATTED = PREFIX + "code_formatted";
	private static final String CODE_COMPACTED = PREFIX + "code_compacted";
	private static final String ICPC2_CODE = PREFIX + "icpc2_code";
	private static final String ICPC2_LABEL = PREFIX + "icpc2_label";
	private static final String LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
	private static final String UMLS_TUI = PREFIX + "umls_tui";
	private static final String UMLS_CONCEPTID = PREFIX + "umls_conceptId";
	private static final String UMLS_ATOMID = PREFIX + "umls_atomId";
	private static final String UMLS_SEMANTICTYPE = PREFIX
			+ "umls_semanticType";
	private static final String EXCLUSION = PREFIX + "exclusion";
	private static final String SYNONYM = PREFIX + "synonym";
	private static final String UNDERTERM = PREFIX + "underterm";
	private static final String INCLUSION = PREFIX + "inclusion";
	private static final String SEEALSO = "http://www.w3.org/2000/01/rdf-schema#seeAlso";

	private String classId;
	private String code_formatted;
	private String code_compacted;
	private String label;
	private String icpc2_code;
	private String icpc2_label;
	private String umls_tui;
	private String umls_conceptId;
	private String umls_atomId;
	private ArrayList<String> umls_semanticType;
	private ArrayList<String> exclusion;
	private ArrayList<String> synonym;
	private ArrayList<String> underterm;
	private ArrayList<String> inclusion;
	private ArrayList<String> seeAlso;

	private static String splitIRI(String iri) {
		int atcDelix = iri.lastIndexOf('/') + 1;
		int icdDelix = iri.lastIndexOf('#') + 1;
		int delim = (atcDelix > icdDelix) ? atcDelix : icdDelix;
		return iri.substring(delim);
	}

	public OWL_Class() {
		umls_semanticType = new ArrayList<String>();
		synonym = new ArrayList<String>();
		underterm = new ArrayList<String>();
		inclusion = new ArrayList<String>();
		exclusion = new ArrayList<String>();
		seeAlso = new ArrayList<String>();
	}

	public static OWL_Class parseOwlClass(OWLOntology o, OWLClass cls) {
		OWL_Class c = new OWL_Class();
		String annoString = null;
		OWLLiteral owlLiteral;
		c.classId = splitIRI(cls.toStringID());
		for (OWLAnnotationProperty a : o.getAnnotationPropertiesInSignature()) {
			for (OWLAnnotation canno : cls.getAnnotations(o, a)) {
				if (canno.getValue() instanceof OWLLiteral) {
					owlLiteral = (OWLLiteral) canno.getValue();
					annoString = owlLiteral.getLiteral();
				} else if (canno.getValue() instanceof IRI) {
					annoString = splitIRI(canno.getValue().toString());
				}
				OWLAnnotationProperty anno = canno.getProperty();
				if (anno.toStringID().equals(SYNONYM)) {
					c.synonym.add(annoString);
				} else if (anno.toStringID().equals(CODE_COMPACTED)) {
					c.code_compacted = annoString;
				} else if (anno.toStringID().equals(LABEL)) {
					c.label = annoString;
				} else if (anno.toStringID().equals(CODE_FORMATTED)) {
					c.code_formatted = annoString;
				} else if (anno.toStringID().equals(UMLS_TUI)) {
					c.umls_tui = annoString;
				} else if (anno.toStringID().equals(UMLS_CONCEPTID)) {
					c.umls_conceptId = annoString;
				} else if (anno.toStringID().equals(UMLS_ATOMID)) {
					c.umls_atomId = annoString;
				} else if (anno.toStringID().equals(UNDERTERM)) {
					c.underterm.add(annoString);
				} else if (anno.toStringID().equals(INCLUSION)) {
					c.inclusion.add(annoString);
				} else if (anno.toStringID().equals(ICPC2_CODE)) {
					c.icpc2_code = annoString;
				} else if (anno.toStringID().equals(ICPC2_LABEL)) {
					c.icpc2_label = annoString;
				} else if (anno.toStringID().equals(UMLS_SEMANTICTYPE)) {
					c.umls_semanticType.add(annoString);
				} else if (anno.toStringID().equals(SEEALSO)) {
					c.seeAlso.add(annoString);
				} else if (anno.toStringID().equals(EXCLUSION)) {
					c.exclusion.add(annoString);
				} else {
					System.err.println("OWLAnnotationProperty not expected: "
							+ anno.toStringID());
				}
			}
		}
		return c;
	}

	public String toString() {
		String str = new String();
		if (classId != null) {
			str += "classId: " + classId + "\n";
		}
		if (code_formatted != null) {
			str += "code_formatted: " + code_formatted + "\n";
		}
		if (code_compacted != null) {
			str += "code_compacted: " + code_compacted + "\n";
		}
		if (label != null) {
			str += "label: " + label + "\n";
		}
		if (umls_tui != null) {
			str += "umls_tui: " + umls_tui + "\n";
		}
		if (umls_conceptId != null) {
			str += "umls_conceptId: " + umls_conceptId + "\n";
		}
		if (umls_atomId != null) {
			str += "umls_atomId: " + umls_atomId + "\n";
		}
		if (icpc2_code != null) {
			str += "icpc2_code: " + icpc2_code + "\n";
		}
		if (icpc2_label != null) {
			str += "icpc2_label: " + icpc2_label + "\n";
		}
		if (umls_semanticType != null && !umls_semanticType.isEmpty()) {
			str += "umls_semanticType: ";
			for (String v : umls_semanticType) {
				str += v + " ";
			}
			str += "\n";
		}
		if (synonym != null && !synonym.isEmpty()) {
			str += "synonym: ";
			for (String v : synonym) {
				str += v + " ";
			}
			str += "\n";
		}
		if (underterm != null && !underterm.isEmpty()) {
			str += "underterm: ";
			for (String v : underterm) {
				str += v + " ";
			}
			str += "\n";
		}
		if (inclusion != null && !inclusion.isEmpty()) {
			str += "inclusion: ";
			for (String v : inclusion) {
				str += v + " ";
			}
			str += "\n";
		}
		if (exclusion != null && !exclusion.isEmpty()) {
			str += "exclusion: ";
			for (String v : exclusion) {
				str += v + " ";
			}
			str += "\n";
		}
		if (seeAlso != null && !seeAlso.isEmpty()) {
			str += "seeAlso: ";
			for (String v : seeAlso) {
				str += v + " ";
			}
			str += "\n";
		}
		return str;
	}

	public String getID() {
		if (code_formatted != null) {
			return code_formatted;
		} else if (code_compacted != null) {
			return code_compacted;
		}
		return classId;
	}

	public String getText() {
		String text = new String();
		text += "\n";
		if (underterm != null && !underterm.isEmpty()) {
			for (String v : underterm) {
				text += v + " ";
			}
			text += "\n";
		}
		text += label + "\n";
		if (synonym != null && !synonym.isEmpty()) {
			for (String v : synonym) {
				text += v + " ";
			}
			text += "\n";
		}
//		if (inclusion != null && !inclusion.isEmpty()) {
//			for (String v : inclusion) {
//				text += v + " ";
//			}
//			text += "\n";
//		}
//		if (umls_semanticType != null && !umls_semanticType.isEmpty()) {
//			for (String v : umls_semanticType) {
//				text += v + " ";
//			}
//			text += "\n";
//		}
		text = text.replace("-", " ");
		text = text.replace("/", " ");
		text = text.replaceAll("[0-9]", "");
		text = text.replaceAll("[^\\p{L}\\p{N}\\s]", "");
		return text;
	}
}
