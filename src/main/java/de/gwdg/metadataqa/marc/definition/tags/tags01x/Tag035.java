package de.gwdg.metadataqa.marc.definition.tags.tags01x;

import de.gwdg.metadataqa.marc.definition.*;
import de.gwdg.metadataqa.marc.definition.general.parser.LinkageParser;
import de.gwdg.metadataqa.marc.definition.general.parser.RecordControlNumberParser;
import static de.gwdg.metadataqa.marc.definition.FRBRFunction.*;

import java.util.Arrays;

/**
 * System Control Number
 * http://www.loc.gov/marc/bibliographic/bd035.html
 */
public class Tag035 extends DataFieldDefinition {

  private static Tag035 uniqueInstance;

  private Tag035() {
    initialize();
    postCreation();
  }

  public static Tag035 getInstance() {
    if (uniqueInstance == null)
      uniqueInstance = new Tag035();
    return uniqueInstance;
  }

  private void initialize() {

    tag = "035";
    label = "System Control Number";
    bibframeTag = "IdentifiedBy/Local";
    mqTag = "SystemControlNumber";
    cardinality = Cardinality.Repeatable;
    descriptionUrl = "https://www.loc.gov/marc/bibliographic/bd035.html";
    setLevels("O");

    ind1 = new Indicator();
    ind2 = new Indicator();

    setSubfieldsWithCardinality(
      "a", "System control number", "NR",
      "z", "Canceled/invalid control number", "R",
      "6", "Linkage", "NR",
      "8", "Field link and sequence number", "R"
    );

    getSubfield("a").setContentParser(RecordControlNumberParser.getInstance());
    getSubfield("6").setContentParser(LinkageParser.getInstance());

    getSubfield("a")
      .setBibframeTag("rdf:value")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("M");

    getSubfield("z")
      .setMqTag("canceled")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("A");

    getSubfield("6")
      .setBibframeTag("linkage")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("A");

    getSubfield("8")
      .setMqTag("fieldLink")
      .setFrbrFunctions(ManagementIdentify, ManagementProcess)
      .setLevels("O");

    putVersionSpecificSubfields(MarcVersion.FENNICA, Arrays.asList(
      new SubfieldDefinition("9", "Voyager-osakenttä", "NR")
    ));
  }
}
