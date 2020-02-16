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

package pt.ua.fairdata.fairdatapoint.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import pt.ua.fairdata.fairdatapoint.service.FairMetaDataService;
import pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException;
import pt.ua.fairdata.fairmetadata4j.io.CatalogMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.DatasetMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.DistributionMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.FDPMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.MetadataException;
import pt.ua.fairdata.fairmetadata4j.io.MetadataParserException;
import pt.ua.fairdata.fairmetadata4j.model.CatalogMetadata;
import pt.ua.fairdata.fairmetadata4j.model.DistributionMetadata;
import pt.ua.fairdata.fairmetadata4j.model.FDPMetadata;
import pt.ua.fairdata.fairmetadata4j.model.Identifier;
import pt.ua.fairdata.fairmetadata4j.utils.MetadataParserUtils;
import pt.ua.fairdata.fairmetadata4j.utils.vocabulary.DataCite;

public class FDPUtils {
	private static final Logger log = Logger.getLogger(FDPUtils.class);
	public static final String FDP_METADATA_FILE = "dtl-fdp.ttl";
	public static final String CATALOG_METADATA_FILE = "textmining-catalog.ttl";
	public static final String CATALOG_ID = "textmining";
	public static final String DATASET_METADATA_FILE = "gda-lumc.ttl";
	public static final String DATASET_ID = "gene-disease-association_lumc";
	public static final String DISTRIBUTION_METADATA_FILE = "gda-lumc-sparql.ttl";
	public static final String DISTRIBUTION_ID = "sparql";
	public final static String FDP_URI = "http://localhost/fdp";
	public final static String CATALOG_URI = "http://localhost/fdp/textmining";
	public final static String DATASET_URI = "http://localhost/fdp/textmining/gene-disease-association_lumc";
	public final static String DISTRIBUTION_URI = "http://localhost/fdp/textmining/gene-disease-association_lumc/sparql";
	public final static String BASE_URI = "http://localhost/";
	public static final String VALID_TEST_FILE = "valid-test-file.ttl";
	public static final RDFFormat FILE_FORMAT = RDFFormat.TURTLE;

	// Read a turtle file and return the content as a string
	public static String getFileContentAsString(String fileName) {
		String content = "";
		try {
			URL fileURL = Paths.get(fileName).toUri().toURL();
			content = Resources.toString(fileURL, Charsets.UTF_8);
		} catch (IOException ex) {
			log.error("Error getting turle file", ex);
		}
		return content;
	}

	// Read a turtle file and return the content as a list of statements
	public static List<Statement> getFileContentAsStatements(String fileName, String baseURI) {
		List<Statement> statements = null;
		try {
			String content = getFileContentAsString(fileName);
			StringReader reader = new StringReader(content);
			Model model;
			model = Rio.parse(reader, baseURI, FILE_FORMAT);
			Iterator<Statement> it = model.iterator();
			statements = Lists.newArrayList(it);
		} catch (IOException | RDFParseException | UnsupportedRDFormatException ex) {
			log.error("Error getting turle file", ex);
		}
		return statements;
	}

	public static FDPMetadata getFDPMetadata(String uri) {
		FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
		ValueFactory f = SimpleValueFactory.getInstance();
		FDPMetadata metadata = parser.parse(getFileContentAsStatements(FDP_METADATA_FILE, uri), f.createIRI(uri));
		return metadata;
	}

	public static CatalogMetadata getCatalogMetadata(String uri, String parentURI) {
		CatalogMetadataParser parser = MetadataParserUtils.getCatalogParser();
		ValueFactory f = SimpleValueFactory.getInstance();
		CatalogMetadata metadata = parser.parse(getFileContentAsStatements(CATALOG_METADATA_FILE, uri),
				f.createIRI(uri));
		metadata.setParentURI(f.createIRI(parentURI));
		return metadata;
	}

	public static pt.ua.fairdata.fairmetadata4j.model.DatasetMetadata getDatasetMetadata(String uri, String parentURI) {
		DatasetMetadataParser parser = MetadataParserUtils.getDatasetParser();
		ValueFactory f = SimpleValueFactory.getInstance();
		pt.ua.fairdata.fairmetadata4j.model.DatasetMetadata metadata = parser
				.parse(getFileContentAsStatements(DATASET_METADATA_FILE, uri), f.createIRI(uri));
		metadata.setParentURI(f.createIRI(parentURI));
		return metadata;
	}

	public static DistributionMetadata getDistributionMetadata(String uri, String parentURI) {
		DistributionMetadataParser parser = MetadataParserUtils.getDistributionParser();
		ValueFactory f = SimpleValueFactory.getInstance();
		DistributionMetadata metadata = parser.parse(getFileContentAsStatements(DISTRIBUTION_METADATA_FILE, uri),
				f.createIRI(uri));
		metadata.setParentURI(f.createIRI(parentURI));
		return metadata;
	}

	public static void storeDefaultFDPMetadata(String fdpUrl, FairMetaDataService fairMetaDataService)
			throws MetadataParserException, MalformedURLException, MetadataException, FairMetadataServiceException {
		FDPMetadata metadata = FDPUtils.getDefaultFDPMetadata(fdpUrl);
		fairMetaDataService.storeFDPMetaData(metadata);
	}

	public static FDPMetadata getDefaultFDPMetadata(String url) throws MalformedURLException {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		String host = new URL(url).getAuthority();
		FDPMetadata metadata = new FDPMetadata();
		metadata.setUri(valueFactory.createIRI(url));
		metadata.setTitle(valueFactory.createLiteral(("FDP of " + host), XMLSchema.STRING));
		metadata.setDescription(valueFactory.createLiteral(("FDP of " + host), XMLSchema.STRING));
		metadata.setLanguage(valueFactory.createIRI("http://id.loc.gov/vocabulary/iso639-1/en"));
		metadata.setLicense(valueFactory.createIRI("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"));
		metadata.setVersion(valueFactory.createLiteral("1.0", XMLSchema.FLOAT));
		metadata.setSwaggerDoc(valueFactory.createIRI(url + "/swagger-ui.html"));
		metadata.setInstitutionCountry(valueFactory.createIRI("http://lexvo.org/id/iso3166/PT"));
		Identifier id = new Identifier();
		id.setUri(valueFactory.createIRI(url + "/metadataID"));
		id.setIdentifier(valueFactory.createLiteral("fdp-metadataID", XMLSchema.STRING));
		id.setType(DataCite.RESOURCE_IDENTIFIER);
		metadata.setIdentifier(id);
		pt.ua.fairdata.fairmetadata4j.model.Agent publisher = new pt.ua.fairdata.fairmetadata4j.model.Agent();
		publisher.setUri(valueFactory.createIRI("http://www.ua.pt"));
		publisher.setType(org.eclipse.rdf4j.model.vocabulary.FOAF.ORGANIZATION);
		publisher.setName(valueFactory.createLiteral("UA", XMLSchema.STRING));
		metadata.setPublisher(publisher);
		metadata.setInstitution(publisher);
		Identifier repoId = new Identifier();
		repoId.setUri(valueFactory.createIRI(url + "/repoID"));
		repoId.setIdentifier(valueFactory.createLiteral("fdp-repoID", XMLSchema.STRING));
		repoId.setType(DataCite.RESOURCE_IDENTIFIER);
		metadata.setRepostoryIdentifier(repoId);
		return metadata;
	}

	public static String getRequesedURL(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	public static String trimmer(String str) {
		String trimmedStr = str;
		trimmedStr = trimmedStr.trim();
		trimmedStr = trimmedStr.replace(" ", "-");
		return trimmedStr;
	}
}
