package de.gwdg.metadataqa.marc.definition.tags.tags01x;

import de.gwdg.metadataqa.marc.definition.Cardinality;
import de.gwdg.metadataqa.marc.definition.DataFieldDefinition;
import de.gwdg.metadataqa.marc.definition.Indicator;
import de.gwdg.metadataqa.marc.definition.general.parser.LinkageParser;
import de.gwdg.metadataqa.marc.definition.general.validator.ISSNValidator;
import static de.gwdg.metadataqa.marc.definition.FRBRFunction.*;

/**
 * International Standard Serial Number
 * http://www.loc.gov/marc/bibliographic/bd022.html
 */
public class Tag022 extends DataFieldDefinition {

  private static Tag022 uniqueInstance;

  private Tag022() {
    initialize();
    postCreation();
  }

  public static Tag022 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag022();
    return uniqueInstance;
  }

  private void initialize() {

    tag = "022";
    label = "International Standard Serial Number";
    bibframeTag = "Issn";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://www.loc.gov/marc/bibliographic/bd022.html";
    setLevels("A", "A");

    ind1 = new Indicator("Level of international interest")
      .setCodes(
        " ", "No level specified",
        "0", "Continuing resource of international interest",
        "1", "Continuing resource not of international interest"
      )
      .setMqTag("levelOfInternationalInterest")
      .setFrbrFunctions(DiscoverySelect, ManagementProcess);

    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "International Standard Serial Number", "NR",
      "l", "ISSN-L", "NR",
      "m", "Canceled ISSN-L", "R",
      "y", "Incorrect ISSN", "R",
      "z", "Canceled ISSN", "R",
      "2", "Source", "NR",
      "6", "Linkage", "NR",
      "8", "Field link and sequence number", "R"
    );
    // TODO check against ISSN National Centres code list http://www.issn.org/
    // getSubfield("2").setCodeList();
    getSubfield("6").setContentParser(LinkageParser.getInstance());

    // TODO: what about the rest of the fields?
    getSubfield("a").setValidator(ISSNValidator.getInstance());


    getSubfield("a")
      .setBibframeTag("rdf:value")
      .setFrbrFunctions(DiscoverySearch, DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A", "A");

    getSubfield("l")
      .setBibframeTag("issnL")
      .setLevels("A", "A");

    getSubfield("m")
      .setMqTag("canceledIssnL")
      .setLevels("A", "A");

    getSubfield("y")
      .setMqTag("incorrect")
      .setFrbrFunctions(DiscoverySearch, DiscoveryIdentify)
      .setLevels("A", "A");

    getSubfield("z")
      .setMqTag("canceled")
      .setFrbrFunctions(DiscoverySearch, DiscoveryIdentify, DiscoveryObtain)
      .setLevels("A", "A");

    getSubfield("2")
      .setMqTag("source")
      .setFrbrFunctions(DiscoveryIdentify, ManagementIdentify, ManagementProcess)
      .setLevels("A", "A");

    getSubfield("6")
      .setBibframeTag("linkage")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("A", "A");

    getSubfield("8")
      .setMqTag("fieldLink")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("O");

    setHistoricalSubfields(
      "b", "Form of issue [OBSOLETE] [CAN/MARC only]",
      "c", "Price [OBSOLETE] [CAN/MARC only]"
    );
  }
}
