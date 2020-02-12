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

/**
 * Contains references to the example metadata rdf files which are used in the
 * Junit tests.
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-08-10
 * @version 0.1
 */
public class FDPUtils {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FDPUtils.class);
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
	public static final org.eclipse.rdf4j.rio.RDFFormat FILE_FORMAT = org.eclipse.rdf4j.rio.RDFFormat.TURTLE;

	/**
	 * Method to read the content of a turtle file
	 * 
	 * @param fileName Turtle file name
	 * @return File content as a string
	 */
	public static String getFileContentAsString(String fileName) {
		String content = "";
		try {
			java.net.URL fileURL = java.nio.file.Paths.get(fileName).toUri().toURL();
			content = com.google.common.io.Resources.toString(fileURL, com.google.common.base.Charsets.UTF_8);
		} catch (java.io.IOException ex) {
			log.error("Error getting turle file", ex);
		}
		return content;
	}

	/**
	 * Method to read the content of a turtle file
	 * 
	 * @param fileName Turtle file name
	 * @param baseURI
	 * @return File content as a string
	 */
	public static java.util.List<org.eclipse.rdf4j.model.Statement> getFileContentAsStatements(String fileName,
			String baseURI) {
		java.util.List<org.eclipse.rdf4j.model.Statement> statements = null;
		try {
			String content = getFileContentAsString(fileName);
			java.io.StringReader reader = new java.io.StringReader(content);
			org.eclipse.rdf4j.model.Model model;
			model = org.eclipse.rdf4j.rio.Rio.parse(reader, baseURI, FILE_FORMAT);
			java.util.Iterator<org.eclipse.rdf4j.model.Statement> it = model.iterator();
			statements = com.google.common.collect.Lists.newArrayList(it);
		} catch (java.io.IOException | org.eclipse.rdf4j.rio.RDFParseException
				| org.eclipse.rdf4j.rio.UnsupportedRDFormatException ex) {
			log.error("Error getting turle file", ex);
		}
		return statements;
	}

	public static nl.dtl.fairmetadata4j.model.FDPMetadata getFDPMetadata(String uri) {
		nl.dtl.fairmetadata4j.io.FDPMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getFdpParser();
		org.eclipse.rdf4j.model.ValueFactory f = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance();
		nl.dtl.fairmetadata4j.model.FDPMetadata metadata = parser
				.parse(getFileContentAsStatements(FDP_METADATA_FILE, uri), f.createIRI(uri));
		return metadata;
	}

	public static nl.dtl.fairmetadata4j.model.CatalogMetadata getCatalogMetadata(String uri, String parentURI) {
		nl.dtl.fairmetadata4j.io.CatalogMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getCatalogParser();
		org.eclipse.rdf4j.model.ValueFactory f = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance();
		nl.dtl.fairmetadata4j.model.CatalogMetadata metadata = parser
				.parse(getFileContentAsStatements(CATALOG_METADATA_FILE, uri), f.createIRI(uri));
		metadata.setParentURI(f.createIRI(parentURI));
		return metadata;
	}

	public static nl.dtl.fairmetadata4j.model.DatasetMetadata getDatasetMetadata(String uri, String parentURI) {
		nl.dtl.fairmetadata4j.io.DatasetMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getDatasetParser();
		org.eclipse.rdf4j.model.ValueFactory f = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance();
		nl.dtl.fairmetadata4j.model.DatasetMetadata metadata = parser
				.parse(getFileContentAsStatements(DATASET_METADATA_FILE, uri), f.createIRI(uri));
		metadata.setParentURI(f.createIRI(parentURI));
		return metadata;
	}

	public static nl.dtl.fairmetadata4j.model.DistributionMetadata getDistributionMetadata(String uri,
			String parentURI) {
		nl.dtl.fairmetadata4j.io.DistributionMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getDistributionParser();
		org.eclipse.rdf4j.model.ValueFactory f = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance();
		nl.dtl.fairmetadata4j.model.DistributionMetadata metadata = parser
				.parse(getFileContentAsStatements(DISTRIBUTION_METADATA_FILE, uri), f.createIRI(uri));
		metadata.setParentURI(f.createIRI(parentURI));
		return metadata;
	}

	/**
	 * Create and store generic FDP metadata
	 *
	 * @param request HttpServletRequest
	 * @throws MetadataParserException
	 */
	public static void storeDefaultFDPMetadata(String fdpUrl,
			pt.ua.fairdata.fairdatapoint.service.FairMetaDataService fairMetaDataService)
			throws nl.dtl.fairmetadata4j.io.MetadataParserException, java.net.MalformedURLException,
			nl.dtl.fairmetadata4j.io.MetadataException,
			pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException {
		nl.dtl.fairmetadata4j.model.FDPMetadata metadata = FDPUtils.getDefaultFDPMetadata(fdpUrl);
		fairMetaDataService.storeFDPMetaData(metadata);
	}

	/**
	 * Create and store generic FDP metadata
	 *
	 * @param request HttpServletRequest
	 * @throws MetadataParserException
	 */
	public static nl.dtl.fairmetadata4j.model.FDPMetadata getDefaultFDPMetadata(String url)
			throws java.net.MalformedURLException {
		org.eclipse.rdf4j.model.ValueFactory valueFactory = org.eclipse.rdf4j.model.impl.SimpleValueFactory
				.getInstance();
		String host = new java.net.URL(url).getAuthority();
		nl.dtl.fairmetadata4j.model.FDPMetadata metadata = new nl.dtl.fairmetadata4j.model.FDPMetadata();
		metadata.setUri(valueFactory.createIRI(url));
		metadata.setTitle(
				valueFactory.createLiteral(("FDP of " + host), org.eclipse.rdf4j.model.vocabulary.XMLSchema.STRING));
		metadata.setDescription(
				valueFactory.createLiteral(("FDP of " + host), org.eclipse.rdf4j.model.vocabulary.XMLSchema.STRING));
		metadata.setLanguage(valueFactory.createIRI("http://id.loc.gov/vocabulary/iso639-1/en"));
		metadata.setLicense(valueFactory.createIRI("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"));
		metadata.setVersion(valueFactory.createLiteral("1.0", org.eclipse.rdf4j.model.vocabulary.XMLSchema.FLOAT));
		metadata.setSwaggerDoc(valueFactory.createIRI(url + "/swagger-ui.html"));
		metadata.setInstitutionCountry(valueFactory.createIRI("http://lexvo.org/id/iso3166/PT"));
		nl.dtl.fairmetadata4j.model.Identifier id = new nl.dtl.fairmetadata4j.model.Identifier();
		id.setUri(valueFactory.createIRI(url + "/metadataID"));
		id.setIdentifier(
				valueFactory.createLiteral("fdp-metadataID", org.eclipse.rdf4j.model.vocabulary.XMLSchema.STRING));
		//// id.setType(nl.dtl.fairmetadata4j.utils.vocabulary.DataCite.RESOURCE_IDENTIFIER);
		metadata.setIdentifier(id);
		nl.dtl.fairmetadata4j.model.Agent publisher = new nl.dtl.fairmetadata4j.model.Agent();
		publisher.setUri(valueFactory.createIRI("http://www.ua.pt"));
		publisher.setType(org.eclipse.rdf4j.model.vocabulary.FOAF.ORGANIZATION);
		publisher.setName(valueFactory.createLiteral("UA", org.eclipse.rdf4j.model.vocabulary.XMLSchema.STRING));
		metadata.setPublisher(publisher);
		metadata.setInstitution(publisher);
		nl.dtl.fairmetadata4j.model.Identifier repoId = new nl.dtl.fairmetadata4j.model.Identifier();
		repoId.setUri(valueFactory.createIRI(url + "/repoID"));
		repoId.setIdentifier(
				valueFactory.createLiteral("fdp-repoID", org.eclipse.rdf4j.model.vocabulary.XMLSchema.STRING));
		//// repoId.setType(nl.dtl.fairmetadata4j.utils.vocabulary.DataCite.RESOURCE_IDENTIFIER);
		metadata.setRepostoryIdentifier(repoId);
		return metadata;
	}

	/**
	 * Get requested URL
	 *
	 * @param request HttpServletRequest
	 * @return URL as a string
	 */
	public static String getRequesedURL(javax.servlet.http.HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

	/**
	 * Trim white space at start, end and between strings
	 *
	 * @param str Input string
	 * @return Trimmed string
	 */
	public static String trimmer(String str) {
		String trimmedStr = str;
		trimmedStr = trimmedStr.trim();
		trimmedStr = trimmedStr.replace(" ", "-");
		return trimmedStr;
	}
}
