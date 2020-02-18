/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pt.ua.scaleus.metadata;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class Utils {
	private static final RepositoryParser fdpParser = new RepositoryParser();
	private static final CatalogParser catalogParser = new CatalogParser();
	private static final DatasetParser datasetParser = new DatasetParser();
	private static final DistributionParser distributionParser = new DistributionParser();
	private static final DataRecordParser dataRecordParser = new DataRecordParser();

	public static RepositoryParser getFdpParser() {
		return fdpParser;
	}

	public static CatalogParser getCatalogParser() {
		return catalogParser;
	}

	public static DatasetParser getDatasetParser() {
		return datasetParser;
	}

	public static DistributionParser getDistributionParser() {
		return distributionParser;
	}

	public static DataRecordParser getDataRecordParser() {
		return dataRecordParser;
	}

	// RDF statements from Metadata
	public static <T extends Metadata> List<Statement> getStatements(@Nonnull T metadata) throws Exception {
		Preconditions.checkNotNull(metadata, "Metadata object must not be null.");
		try {
			checkMandatoryProperties(metadata);
		} catch (NullPointerException ex) {
			throw (new Exception(ex.getMessage()));
		}
		Model model = new LinkedHashModel();
		setCommonProperties(model, metadata);
		List<Statement> stms = null;
		if (metadata instanceof Repository) {
			stms = getStatements(model, (Repository) metadata);
		} else if (metadata instanceof Catalog) {
			stms = getStatements(model, (Catalog) metadata);
		} else if (metadata instanceof Dataset) {
			stms = getStatements(model, (Dataset) metadata);
		} else if (metadata instanceof Distribution) {
			stms = getStatements(model, (Distribution) metadata);
		} else if (metadata instanceof DataRecord) {
			stms = getStatements(model, (DataRecord) metadata);
		}
		return stms;
	}

	// RDF string from Metadata
	public static <T extends Metadata> String getString(@Nonnull T metadata, @Nonnull RDFFormat format)
			throws Exception {
		Preconditions.checkNotNull(metadata, "Metadata object must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		StringWriter sw = new StringWriter();
		RDFWriter writer = Rio.createWriter(format, sw);
		List<Statement> statement = getStatements(metadata);
		try {
			propagateToHandler(statement, writer);
		} catch (org.eclipse.rdf4j.repository.RepositoryException | org.eclipse.rdf4j.rio.RDFHandlerException ex) {
			throw (new Exception(ex.getMessage()));
		}
		return sw.toString();
	}

	// RDF statements from Repository
	private static List<Statement> getStatements(Model model, Repository metadata) throws Exception {
		try {
			Preconditions.checkNotNull(metadata.getPublisher(), "Metadata publisher must not be null.");
		} catch (NullPointerException ex) {
			throw (new Exception(ex.getMessage()));
		}
		ValueFactory f = SimpleValueFactory.getInstance();
		addStatement(model, metadata.getUri(), RDF.TYPE, R3D.TYPE_REPOSTORY);
		metadata.getCatalogs().stream().forEach((catalog) -> {
			addStatement(model, metadata.getUri(), R3D.DATA_CATALOG, catalog);
		});
		addAgentStatements(model, metadata.getUri(), R3D.INSTITUTION, metadata.getInstitution());
		return getStatements(model);
	}

	// RDF statements from Catalog
	private static List<Statement> getStatements(Model model, Catalog metadata) throws Exception {
		try {
			Preconditions.checkNotNull(metadata.getPublisher(), "Metadata publisher must not be null.");
		} catch (NullPointerException | IllegalArgumentException ex) {
			throw (new Exception(ex.getMessage()));
		}
		addStatement(model, metadata.getUri(), RDF.TYPE, DCAT.TYPE_CATALOG);
		addStatement(model, metadata.getUri(), FOAF.HOMEPAGE, metadata.getHomepage());
		metadata.getDatasets().stream().forEach((dataset) -> {
			addStatement(model, metadata.getUri(), DCAT.DATASET, dataset);
		});
		return getStatements(model);
	}

	// RDF statements from Dataset
	private static List<Statement> getStatements(Model model, Dataset metadata) throws Exception {
		try {
			Preconditions.checkNotNull(metadata.getPublisher(), "Metadata publisher must not be null.");
		} catch (NullPointerException | IllegalArgumentException ex) {
			throw (new Exception(ex.getMessage()));
		}
		addStatement(model, metadata.getUri(), RDF.TYPE, DCAT.TYPE_DATASET);
		metadata.getDistributions().stream().forEach((distribution) -> {
			addStatement(model, metadata.getUri(), DCAT.DISTRIBUTION, distribution);
		});
		return getStatements(model);
	}

	// RDF statements from Distribution
	private static java.util.List<Statement> getStatements(Model model, Distribution metadata) throws Exception {
		if (metadata.getUri() == null && metadata.getDownloadURL() == null) {
			throw (new Exception("No dcat:accessURL or dcat:downloadURL URL is provided"));
		}
		addStatement(model, metadata.getUri(), RDF.TYPE, DCAT.TYPE_DISTRIBUTION);
		addStatement(model, metadata.getUri(), DCAT.DOWNLOAD_URL, metadata.getDownloadURL());
		addStatement(model, metadata.getUri(), DCAT.FORMAT, metadata.getFormat());
		return getStatements(model);
	}

	// RDF statements from DataRecord
	private static List<Statement> getStatements(Model model, DataRecord metadata) throws Exception {
		try {
			Preconditions.checkNotNull(metadata.getRmlURI(), "Metadata rml mapping uri must not be null.");
		} catch (NullPointerException | IllegalArgumentException ex) {
			throw (new Exception(ex.getMessage()));
		}
		addStatement(model, metadata.getUri(), RDF.TYPE, DCAT.TYPE_DISTRIBUTION);
		addStatement(model, metadata.getUri(), FDP.RML_MAPPING, metadata.getRmlURI());
		addStatement(model, metadata.getUri(), FDP.REFERS_TO, metadata.getDistributionURI());
		addStatement(model, metadata.getUri(), DCTERMS.ISSUED, metadata.getDataRecordIssued());
		addStatement(model, metadata.getUri(), DCTERMS.MODIFIED, metadata.getDataRecordModified());
		return getStatements(model);
	}

	// RDF statements from RDF model
	private static java.util.List<Statement> getStatements(Model model) {
		Iterator<Statement> it = model.iterator();
		List<Statement> statements = ImmutableList.copyOf(it);
		return statements;
	}

	// Set common metadata properties. (E.g) title, version etc
	private static void setCommonProperties(Model model, Metadata metadata) {
		addStatement(model, metadata.getUri(), DCTERMS.TITLE, metadata.getTitle());
		addStatement(model, metadata.getUri(), RDFS.LABEL, metadata.getTitle());
		addStatement(model, metadata.getUri(), DCTERMS.HAS_VERSION, metadata.getVersion());
		addStatement(model, metadata.getUri(), FDP.METADATA_ISSUED, metadata.getIssued());
		addIdStatements(model, metadata.getUri(), FDP.METADATA_IDENTIFIER, metadata.getIdentifier());
		addStatement(model, metadata.getUri(), FDP.METADATA_MODIFIED, metadata.getModified());
		addStatement(model, metadata.getUri(), DCTERMS.LANGUAGE, metadata.getLanguage());
		addAgentStatements(model, metadata.getUri(), DCTERMS.PUBLISHER, metadata.getPublisher());
		addStatement(model, metadata.getUri(), DCTERMS.LANGUAGE, metadata.getLanguage());
		addStatement(model, metadata.getUri(), DCTERMS.DESCRIPTION, metadata.getDescription());
		addStatement(model, metadata.getUri(), DCTERMS.LICENSE, metadata.getLicense());
		addStatement(model, metadata.getUri(), DCTERMS.RIGHTS, metadata.getRights());
		addStatement(model, metadata.getUri(), DCTERMS.IS_PART_OF, metadata.getParentURI());
	}

	// Check if the metadata object contains mandatory metadata properties
	private static void checkMandatoryProperties(Metadata metadata) throws Exception {
		Preconditions.checkNotNull(metadata.getIdentifier(), "Metadata ID must not be null.");
		Preconditions.checkNotNull(metadata.getIdentifier().getIdentifier(), "Metadata ID literal must not be null.");
		Preconditions.checkNotNull(metadata.getIdentifier().getUri(), "Metadata ID uri must not be null.");
		Preconditions.checkNotNull(metadata.getIdentifier().getType(), "Metadata ID type must not be null.");
		Preconditions.checkNotNull(metadata.getTitle(), "Metadata title must not be null.");
		Preconditions.checkNotNull(metadata.getVersion(), "Metadata version must not be null.");
		Preconditions.checkNotNull(metadata.getIssued(), "Metadata issued date must not be null.");
		Preconditions.checkNotNull(metadata.getModified(), "Metadata modified date must not be null.");
	}

	private static void propagateToHandler(List<Statement> statements, RDFHandler handler)
			throws RDFHandlerException, RepositoryException {
		handler.startRDF();
		handler.handleNamespace(RDF.PREFIX, RDF.NAMESPACE);
		handler.handleNamespace(RDFS.PREFIX, RDFS.NAMESPACE);
		handler.handleNamespace(DCAT.PREFIX, DCAT.NAMESPACE);
		handler.handleNamespace(XMLSchema.PREFIX, XMLSchema.NAMESPACE);
		handler.handleNamespace(OWL.PREFIX, OWL.NAMESPACE);
		handler.handleNamespace(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
		handler.handleNamespace(FDP.PREFIX, FDP.NAMESPACE);
		handler.handleNamespace(R3D.PREFIX, R3D.NAMESPACE);
		handler.handleNamespace("lang", "http://id.loc.gov/vocabulary/iso639-1/");
		for (Statement st : statements) {
			handler.handleStatement(st);
		}
		handler.endRDF();
	}

	// We are using this method to reduce the NPath complexity
	// Add id instance's rdf statements
	private static void addIdStatements(Model model, IRI subj, IRI pred, Identifier objc) {
		if (objc != null) {
			addStatement(model, subj, pred, objc.getUri());
			addStatement(model, objc.getUri(), RDF.TYPE, objc.getType());
			addStatement(model, objc.getUri(), DCTERMS.IDENTIFIER, objc.getIdentifier());
		}
	}

	// We are using this method to reduce the NPath complexity
	// Add agent instance's rdf statements
	private static void addAgentStatements(Model model, IRI subj, IRI pred, Agent objc) {
		if (objc != null) {
			addStatement(model, subj, pred, objc.getUri());
			addStatement(model, objc.getUri(), RDF.TYPE, objc.getType());
			if (objc.getName() == null) {
				;
			} else {
				addStatement(model, objc.getUri(), FOAF.NAME, objc.getName());
			}
		}
	}

	// We are using this method to reduce the NPath complexity
	// Add rdf statement
	private static void addStatement(Model model, IRI subj, IRI pred, Value objc) {
		if (objc != null) {
			model.add(subj, pred, objc);
		}
	}

	// Create date literal based on current time
	public static Literal getCurrentTime() throws DatatypeConfigurationException {
		Date date = new Date();
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		ValueFactory f = SimpleValueFactory.getInstance();
		Literal currentTime = f.createLiteral(xmlDate.toXMLFormat(), XMLSchema.DATETIME);
		return currentTime;
	}

	// Create List<Statements> from rdf string
	public static List<Statement> getStatements(String rdfString, IRI baseUri, RDFFormat format) throws Exception {
		String uri;
		if (baseUri == null) {
			uri = "http://example.com/dummyResource";
		} else {
			uri = baseUri.stringValue();
		}
		try {
			Model model = Rio.parse(new StringReader(rdfString), uri, format);
			Iterator<Statement> it = model.iterator();
			List<Statement> statements = ImmutableList.copyOf(it);
			return statements;
		} catch (RDFParseException | UnsupportedRDFormatException | IOException ex) {
			String errMsg = "Error reading dataset metadata content" + ex.getMessage();
			throw (new Exception(errMsg));
		}
	}
}
