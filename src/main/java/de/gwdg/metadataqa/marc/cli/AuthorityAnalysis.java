package de.gwdg.metadataqa.marc.cli;

import de.gwdg.metadataqa.marc.MarcRecord;
import de.gwdg.metadataqa.marc.Utils;
import de.gwdg.metadataqa.marc.analysis.AuthorithyAnalyzer;
import de.gwdg.metadataqa.marc.analysis.AuthorityStatistics;
import de.gwdg.metadataqa.marc.cli.parameters.CommonParameters;
import de.gwdg.metadataqa.marc.cli.parameters.ValidatorParameters;
import de.gwdg.metadataqa.marc.cli.processor.MarcFileProcessor;
import de.gwdg.metadataqa.marc.cli.utils.RecordIterator;
import de.gwdg.metadataqa.marc.cli.utils.Schema;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import static de.gwdg.metadataqa.marc.Utils.count;

public class AuthorityAnalysis implements MarcFileProcessor, Serializable {

  private static final Logger logger = Logger.getLogger(AuthorityAnalysis.class.getCanonicalName());

  private final Options options;
  private CommonParameters parameters;
  private Map<Integer, Integer> histogram = new HashMap<>();
  private Map<Boolean, Integer> hasClassifications = new HashMap<>();
  private boolean readyToProcess;
  private static char separator = ',';
  AuthorityStatistics statistics = new AuthorityStatistics();

  public AuthorityAnalysis(String[] args) throws ParseException {
    parameters = new ValidatorParameters(args);
    options = parameters.getOptions();
    readyToProcess = true;
  }

  public static void main(String[] args) {
    MarcFileProcessor processor = null;
    try {
      processor = new AuthorityAnalysis(args);
    } catch (ParseException e) {
      System.err.println(createRow("ERROR. ", e.getLocalizedMessage()));
      // processor.printHelp(processor.getParameters().getOptions());
      System.exit(0);
    }
    if (processor.getParameters().getArgs().length < 1) {
      System.err.println("Please provide a MARC file name!");
      processor.printHelp(processor.getParameters().getOptions());
      System.exit(0);
    }
    if (processor.getParameters().doHelp()) {
      processor.printHelp(processor.getParameters().getOptions());
      System.exit(0);
    }
    RecordIterator iterator = new RecordIterator(processor);
    iterator.start();
  }

  @Override
  public CommonParameters getParameters() {
    return parameters;
  }

  @Override
  public void processRecord(Record marc4jRecord, int recordNumber) throws IOException {

  }

  @Override
  public void processRecord(MarcRecord marcRecord, int recordNumber) throws IOException {
    AuthorithyAnalyzer analyzer = new AuthorithyAnalyzer(marcRecord, statistics);
    int count = analyzer.process();
    count((count > 0), hasClassifications);
    count(count, histogram);
  }

  @Override
  public void beforeIteration() {

  }

  @Override
  public void fileOpened(Path path) {

  }

  @Override
  public void fileProcessed() {

  }

  @Override
  public void afterIteration(int numberOfprocessedRecords) {
    printAuthoritiesBySchema();
    printAuthoritiesByRecords();
    printAuthoritiesHistogram();
    printAuthoritiesSubfieldsStatistics();
  }

  private void printAuthoritiesBySchema() {
    Path path = Paths.get(parameters.getOutputDir(), "authorities-by-schema.csv");
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(createRow("id", "field", "location", "scheme", "abbreviation", "abbreviation4solr", "recordcount", "instancecount"));
      statistics.getInstances()
        .entrySet()
        .stream()
        .sorted((e1, e2) -> {
            int i = e1.getKey().getField().compareTo(e2.getKey().getField());
            if (i != 0)
              return i;
            else {
              i = e1.getKey().getLocation().compareTo(e2.getKey().getLocation());
              if (i != i)
                return i;
              else
                return e2.getValue().compareTo(e1.getValue());
            }
          }
        )
        .forEach(
          entry -> printSingleClassificationBySchema(writer, entry)
        );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void printSingleClassificationBySchema(BufferedWriter writer, Map.Entry<Schema, Integer> entry) {
    Schema schema = entry.getKey();
    int instanceCount = entry.getValue();
    int recordCount = statistics.getRecords().get(schema);
    try {
      writer.write(createRow(
        schema.getId(),
        schema.getField(),
        schema.getLocation(),
        '"' + schema.getSchema().replace("\"", "\\\"") + '"',
        schema.getAbbreviation(),
        Utils.solarize(schema.getAbbreviation()),
        recordCount,
        instanceCount
      ));
    } catch (IOException ex) {
      ex.printStackTrace();
      System.err.println(schema);
    } catch (NullPointerException ex) {
      ex.printStackTrace();
      System.err.println(schema);
    }
  }

  private void printAuthoritiesByRecords() {
    Path path;
    path = Paths.get(parameters.getOutputDir(), "authorities-by-records.csv");
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(createRow("records-with-authorities", "count"));
      hasClassifications
        .entrySet()
        .stream()
        .sorted((e1, e2) ->
          e2.getValue().compareTo(e1.getValue()))
        .forEach(
          e -> {
            try {
              writer.write(createRow(e.getKey().toString(), e.getValue()));
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void printAuthoritiesHistogram() {
    Path path = Paths.get(parameters.getOutputDir(), "authorities-histogram.csv");
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(createRow("count", "frequency"));
      histogram
        .entrySet()
        .stream()
        .sorted((e1, e2) -> {
          return e1.getKey().compareTo(e2.getKey());
        })
        .forEach(
          entry -> {
            try {
              writer.write(createRow(entry.getKey(), entry.getValue()));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void printAuthoritiesSubfieldsStatistics() {
    Path path = Paths.get(parameters.getOutputDir(), "authorities-by-schema-subfields.csv");
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      // final List<String> header = Arrays.asList("field", "location", "label", "abbreviation", "subfields", "scount");
      final List<String> header = Arrays.asList("id", "subfields", "count");
      writer.write(createRow(header));
      statistics.getSubfields()
        .entrySet()
        .stream()
        .sorted((e1, e2) ->
          e1.getKey().getField().compareTo(e2.getKey().getField()))
        .forEach(
          schemaEntry -> printSingleSchemaSubfieldsStatistics(writer, schemaEntry)
        );
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void printSingleSchemaSubfieldsStatistics(BufferedWriter writer, Map.Entry<Schema, Map<List<String>, Integer>> schemaEntry) {
    Schema schema = schemaEntry.getKey();
    Map<List<String>, Integer> val = schemaEntry.getValue();
    val
      .entrySet()
      .stream()
      .sorted((count1, count2) -> count2.getValue().compareTo(count1.getValue()))
      .forEach(
        countEntry -> {
          List<String> subfields = countEntry.getKey();
          int count = countEntry.getValue();
          try {
            writer.write(createRow(
              schema.getId(),
              // schema.field,
              // schema.location,
              // '"' + schema.schema.replace("\"", "\\\"") + '"',
              // schema.abbreviation,
              StringUtils.join(subfields, ';'),
              count
            ));
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      );
  }

  private static String createRow(List<String> fields) {
    return StringUtils.join(fields, separator) + "\n";
  }

  private static String createRow(Object... fields) {
    return StringUtils.join(fields, separator) + "\n";
  }

  @Override
  public void printHelp(Options options) {

  }

  @Override
  public boolean readyToProcess() {
    return readyToProcess;
  }
}
