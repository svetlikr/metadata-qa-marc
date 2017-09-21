package de.gwdg.metadataqa.marc.definition.tags76x;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.Indicator;
import de.gwdg.metadataqa.marc.definition.general.codelist.RelatorCodes;
import de.gwdg.metadataqa.marc.definition.general.parser.RecordControlNumberParser;

/**
 * Host Item Entry
 * http://www.loc.gov/marc/bibliographic/bd773.html
 */
public class Tag773 extends DataFieldDefinition {

	private static Tag773 uniqueInstance;

	private Tag773() {
		initialize();
	}

	public static Tag773 getInstance() {
		if (uniqueInstance == null)
			uniqueInstance = new Tag773();
		return uniqueInstance;
	}

	private void initialize() {
		tag = "773";
		label = "Host Item Entry";
		cardinality = Cardinality.Repeatable;
		ind1 = new Indicator("Note controller").setCodes(
			"0", "Display note",
			"1", "Do not display note"
		);
		ind2 = new Indicator("Display constant controller").setCodes(
			" ", "In",
			"8", "No display constant generated"
		);
		setSubfieldsWithCardinality(
			"a", "Main entry heading", "NR",
			"b", "Edition", "NR",
			"d", "Place, publisher, and date of publication", "NR",
			"g", "Related parts", "R",
			"h", "Physical description", "NR",
			"i", "Relationship information", "R",
			"k", "Series data for related item", "R",
			"m", "Material-specific details", "NR",
			"n", "Note", "R",
			"o", "Other item identifier", "R",
			"p", "Abbreviated title", "NR",
			"q", "Enumeration and first page", "NR",
			"r", "Report number", "R",
			"s", "Uniform title", "NR",
			"t", "Title", "NR",
			"u", "Standard Technical Report Number", "NR",
			"w", "Record control number", "R",
			"x", "International Standard Serial Number", "NR",
			"y", "CODEN designation", "NR",
			"z", "International Standard Book Number", "R",
			"3", "Materials specified", "NR",
			"4", "Relationship code", "R",
			"6", "Linkage", "NR",
			"7", "Control subfield", "NR",
			"8", "Field link and sequence number", "R"
		);
		getSubfield("7").setCodes(
			"0", "Type of main entry heading",
			"1", "Form of name",
			"2", "Type of record",
			"3", "Bibliographic level"
		);
		getSubfield("4").setCodeList(RelatorCodes.getInstance());
		getSubfield("w").setContentParser(RecordControlNumberParser.getInstance());
	}
}