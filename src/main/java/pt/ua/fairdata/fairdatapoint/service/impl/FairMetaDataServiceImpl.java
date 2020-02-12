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

package pt.ua.fairdata.fairdatapoint.service.impl;

/**
 * Service layer for manipulating fair metadata
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2015-12-17
 * @version 0.2
 */
public class FairMetaDataServiceImpl implements pt.ua.fairdata.fairdatapoint.service.FairMetaDataService {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FairMetaDataServiceImpl.class);
	private pt.ua.fairdata.fairdatapoint.repository.StoreManager storeManager = new pt.ua.fairdata.fairdatapoint.repository.impl.StoreManagerImpl();

	@Override
	public nl.dtl.fairmetadata4j.model.FDPMetadata retrieveFDPMetaData(
			@javax.annotation.Nonnull org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			org.springframework.data.rest.webmvc.ResourceNotFoundException {
		java.util.List<org.eclipse.rdf4j.model.Statement> statements = retrieveStatements(uri);
		nl.dtl.fairmetadata4j.io.FDPMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getFdpParser();
		nl.dtl.fairmetadata4j.model.FDPMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public nl.dtl.fairmetadata4j.model.CatalogMetadata retrieveCatalogMetaData(
			@javax.annotation.Nonnull org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			org.springframework.data.rest.webmvc.ResourceNotFoundException {
		java.util.List<org.eclipse.rdf4j.model.Statement> statements = retrieveStatements(uri);
		nl.dtl.fairmetadata4j.io.CatalogMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getCatalogParser();
		nl.dtl.fairmetadata4j.model.CatalogMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public nl.dtl.fairmetadata4j.model.DatasetMetadata retrieveDatasetMetaData(
			@javax.annotation.Nonnull org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			org.springframework.data.rest.webmvc.ResourceNotFoundException {
		java.util.List<org.eclipse.rdf4j.model.Statement> statements = retrieveStatements(uri);
		nl.dtl.fairmetadata4j.io.DatasetMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getDatasetParser();
		nl.dtl.fairmetadata4j.model.DatasetMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public nl.dtl.fairmetadata4j.model.DistributionMetadata retrieveDistributionMetaData(
			@javax.annotation.Nonnull org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			org.springframework.data.rest.webmvc.ResourceNotFoundException {
		java.util.List<org.eclipse.rdf4j.model.Statement> statements = retrieveStatements(uri);
		nl.dtl.fairmetadata4j.io.DistributionMetadataParser parser = nl.dtl.fairmetadata4j.utils.MetadataParserUtils
				.getDistributionParser();
		nl.dtl.fairmetadata4j.model.DistributionMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public void storeFDPMetaData(@javax.annotation.Nonnull nl.dtl.fairmetadata4j.model.FDPMetadata metadata)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			nl.dtl.fairmetadata4j.io.MetadataException {
		com.google.common.base.Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
		storeMetadata(metadata);
	}

	@Override
	public void storeCatalogMetaData(
			@javax.annotation.Nonnull nl.dtl.fairmetadata4j.model.CatalogMetadata metadata)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			nl.dtl.fairmetadata4j.io.MetadataException {
		com.google.common.base.Preconditions.checkState(metadata.getParentURI() != null,
				"No fdp URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		// com.google.common.base.Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
		// "The fdp URI doesn't exist in the repository. "
		// + "Please try with valid fdp URI");
		storeMetadata(metadata);
	}

	@Override
	public void storeDatasetMetaData(
			@javax.annotation.Nonnull nl.dtl.fairmetadata4j.model.DatasetMetadata metadata)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			nl.dtl.fairmetadata4j.io.MetadataException {
		com.google.common.base.Preconditions.checkState(metadata.getParentURI() != null,
				"No catalog URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		com.google.common.base.Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
				"The catalogy URI doesn't exist in the repository. " + "Please try with valid catalogy URI");
		storeMetadata(metadata);
	}

	@Override
	public void storeDistributionMetaData(
			@javax.annotation.Nonnull nl.dtl.fairmetadata4j.model.DistributionMetadata metadata)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			nl.dtl.fairmetadata4j.io.MetadataException {
		com.google.common.base.Preconditions.checkState(metadata.getParentURI() != null,
				"No dataset URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		com.google.common.base.Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
				"The dataset URI doesn't exist in the repository. " + "Please try with valid dataset URI");
		storeMetadata(metadata);
	}

	private <T extends nl.dtl.fairmetadata4j.model.Metadata> void storeMetadata(
			@javax.annotation.Nonnull T metadata)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			nl.dtl.fairmetadata4j.io.MetadataException {
		com.google.common.base.Preconditions.checkNotNull(metadata, "Metadata must not be null.");
		com.google.common.base.Preconditions.checkState(!isSubjectURIExist(metadata.getUri()),
				"The metadata URI already exist in the repository. " + "Please try with different ID");
		try {
			if (metadata instanceof nl.dtl.fairmetadata4j.model.FDPMetadata) {
				if (metadata.getIssued() == null) {
					metadata.setIssued(nl.dtl.fairmetadata4j.utils.RDFUtils.getCurrentTime());
				}
			} else {
				metadata.setIssued(nl.dtl.fairmetadata4j.utils.RDFUtils.getCurrentTime());
			}
			metadata.setModified(nl.dtl.fairmetadata4j.utils.RDFUtils.getCurrentTime());
			storeManager.storeStatements(nl.dtl.fairmetadata4j.utils.MetadataUtils.getStatements(metadata));
			updateParentResource(metadata);
		} catch (pt.ua.fairdata.fairdatapoint.repository.StoreManagerException
				| javax.xml.datatype.DatatypeConfigurationException ex) {
			log.error("Error storing distribution metadata");
			throw (new pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException(ex.getMessage()));
		}
	}

	/**
	 * Update properties of parent class. (E.g) dcat:Modified
	 *
	 * @param          <T>
	 * @param metadata Subtype of Metadata object
	 */
	private <T extends nl.dtl.fairmetadata4j.model.Metadata> void updateParentResource(
			@javax.annotation.Nonnull T metadata) {
		com.google.common.base.Preconditions.checkNotNull(metadata, "Metadata object must not be null.");
		try {
			org.eclipse.rdf4j.model.ValueFactory f = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance();
			java.util.List<org.eclipse.rdf4j.model.Statement> stmts = new java.util.ArrayList<>();
			if (metadata instanceof nl.dtl.fairmetadata4j.model.FDPMetadata) {
				return;
			} else if (metadata instanceof nl.dtl.fairmetadata4j.model.CatalogMetadata) {
				stmts.add(f.createStatement(metadata.getParentURI(),
						nl.dtl.fairmetadata4j.utils.vocabulary.R3D.DATACATALOG, metadata.getUri())); // DATA_CATALOG
			} else if (metadata instanceof nl.dtl.fairmetadata4j.model.DatasetMetadata) {
				stmts.add(f.createStatement(metadata.getParentURI(),
						// nl.dtl.fairmetadata4j.utils.vocabulary.DCAT.DATASET, metadata.getUri()));
				        nl.dtl.fairmetadata4j.utils.vocabulary.SCHEMAORG.DATASET, metadata.getUri()));
			} else if (metadata instanceof nl.dtl.fairmetadata4j.model.DistributionMetadata) {
				stmts.add(f.createStatement(metadata.getParentURI(),
						// nl.dtl.fairmetadata4j.utils.vocabulary.DCAT.DISTRIBUTION, metadata.getUri()));
				        nl.dtl.fairmetadata4j.utils.vocabulary.SCHEMAORG.DISTRIBUTION, metadata.getUri()));
			}
			storeManager.removeStatement(metadata.getParentURI(), org.eclipse.rdf4j.model.vocabulary.DCTERMS.MODIFIED,
					null);
			stmts.add(f.createStatement(metadata.getParentURI(), org.eclipse.rdf4j.model.vocabulary.DCTERMS.MODIFIED,
					nl.dtl.fairmetadata4j.utils.RDFUtils.getCurrentTime()));
			storeManager.storeStatements(stmts);
		} catch (pt.ua.fairdata.fairdatapoint.repository.StoreManagerException
				| javax.xml.datatype.DatatypeConfigurationException ex) {
			log.error("Error updating parent resource :" + ex.getMessage());
		}
	}

	private java.util.List<org.eclipse.rdf4j.model.Statement> retrieveStatements(
			@javax.annotation.Nonnull org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			org.springframework.data.rest.webmvc.ResourceNotFoundException {
		try {
			com.google.common.base.Preconditions.checkNotNull(uri, "Resource uri not be null.");
			java.util.List<org.eclipse.rdf4j.model.Statement> statements = storeManager.retrieveResource(uri);
			if (statements.isEmpty()) {
				String msg = ("No metadata found for the uri : " + uri);
				throw (new org.springframework.data.rest.webmvc.ResourceNotFoundException(msg));
			}
			addAddtionalResource(statements);
			return statements;
		} catch (pt.ua.fairdata.fairdatapoint.repository.StoreManagerException ex) {
			log.error("Error retrieving fdp metadata from the store");
			throw (new pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException(ex.getMessage()));
		}
	}

	/**
	 * Check if URI exist in a repository as a subject
	 *
	 * @param uri
	 * @return
	 * @throws FairMetadataServiceException
	 */
	private boolean isSubjectURIExist(@javax.annotation.Nonnull org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException {
		boolean isURIExist = false;
		try {
			isURIExist = storeManager.isStatementExist(uri, null, null);
		} catch (pt.ua.fairdata.fairdatapoint.repository.StoreManagerException ex) {
			log.error("Error checking existence of subject URI");
			throw (new pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException(ex.getMessage()));
		}
		return isURIExist;
	}

	private void addAddtionalResource(java.util.List<org.eclipse.rdf4j.model.Statement> statements)
			throws pt.ua.fairdata.fairdatapoint.repository.StoreManagerException {
		java.util.List<org.eclipse.rdf4j.model.Statement> otherResources = new java.util.ArrayList<>();
		for (org.eclipse.rdf4j.model.Statement st : statements) {
			org.eclipse.rdf4j.model.IRI predicate = st.getPredicate();
			org.eclipse.rdf4j.model.Value object = st.getObject();
			if (predicate.equals(nl.dtl.fairmetadata4j.utils.vocabulary.FDP.METADATAIDENTIFIER)) { // METADATA_IDENTIFIER
				otherResources.addAll(storeManager.retrieveResource((org.eclipse.rdf4j.model.IRI) object));
			} else if (predicate.equals(nl.dtl.fairmetadata4j.utils.vocabulary.R3D.INSTITUTION)) {
				otherResources.addAll(storeManager.retrieveResource((org.eclipse.rdf4j.model.IRI) object));
			} else if (predicate.equals(org.eclipse.rdf4j.model.vocabulary.DCTERMS.PUBLISHER)) {
				otherResources.addAll(storeManager.retrieveResource((org.eclipse.rdf4j.model.IRI) object));
			//FIXME
			//} else if (predicate.equals(nl.dtl.fairmetadata4j.utils.vocabulary.R3D.REPO_IDENTIFIER)) { // REPO_IDENTIFIER
			} else if (false) { // REPO_IDENTIFIER
				otherResources.addAll(storeManager.retrieveResource((org.eclipse.rdf4j.model.IRI) object));
			}
		}
		statements.addAll(otherResources);
	}

	@Override
	public void updateFDPMetaData(org.eclipse.rdf4j.model.IRI uri,
			nl.dtl.fairmetadata4j.model.FDPMetadata metaDataUpdate)
			throws pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException,
			nl.dtl.fairmetadata4j.io.MetadataException {
		nl.dtl.fairmetadata4j.model.FDPMetadata metadata = retrieveFDPMetaData(uri);
		// This is an unconventional way of copying values from a source to a
		// target object, and the original developers are aware of that fact.
		// The original approach used a numer of repeated if-blocks to check for
		// null values before settings, like the following example:
		// if (x.getY() != null) {
		// z.setY(x.getY());
		// }
		// This resulted in an NPath complexity of over 16000. In order to
		// work around the repeated if-blocks, the null check and getter/setter
		// logic is extracted into the setMetadataProperty method.
		setMetadataProperty(metaDataUpdate::getDescription, metadata::setDescription);
		setMetadataProperty(metaDataUpdate::getIdentifier, metadata::setIdentifier);
		setMetadataProperty(metaDataUpdate::getInstitution, metadata::setInstitution);
		setMetadataProperty(metaDataUpdate::getInstitutionCountry, metadata::setInstitutionCountry);
		setMetadataProperty(metaDataUpdate::getLanguage, metadata::setLanguage);
		setMetadataProperty(metaDataUpdate::getLicense, metadata::setLicense);
		setMetadataProperty(metaDataUpdate::getPublisher, metadata::setPublisher);
		setMetadataProperty(metaDataUpdate::getRepostoryIdentifier, metadata::setRepostoryIdentifier);
		setMetadataProperty(metaDataUpdate::getRights, metadata::setRights);
		setMetadataProperty(metaDataUpdate::getStartDate, metadata::setStartDate);
		setMetadataProperty(metaDataUpdate::getSwaggerDoc, metadata::setSwaggerDoc);
		setMetadataProperty(metaDataUpdate::getTitle, metadata::setTitle);
		setMetadataProperty(metaDataUpdate::getVersion, metadata::setVersion);
		try {
			storeManager.removeResource(uri);
			storeFDPMetaData(metadata);
		} catch (pt.ua.fairdata.fairdatapoint.repository.StoreManagerException ex) {
			log.error("Error deleting existence fdp resource");
			throw (new pt.ua.fairdata.fairdatapoint.service.FairMetadataServiceException(ex.getMessage()));
		}
	}

	/**
	 * Convenience method to reduce the NPath complexity measure of the
	 * {@link #updateFDPMetaData(IRI, FDPMetadata)} method.
	 *
	 * @param getter the getter method of the source object.
	 * @param setter the setter method of the target object.
	 */
	private <T> void setMetadataProperty(Getter<T> getter, Setter<T> setter) {
		if (getter.get() != null) {
			setter.set(getter.get());
		}
	}

	/**
	 * Convenience interface to facilitate referring to a getter method as a
	 * function pointer.
	 *
	 * @param <T> datatype of the getter return value.
	 */
	@FunctionalInterface
	private interface Getter<T> {
		T get();
	}

	/**
	 * Convenience interface to facilitate referring to a setter method as a
	 * function pointer.
	 *
	 * @param <T> datatype of the setter parameter.
	 */
	@FunctionalInterface
	private interface Setter<T> {
		void set(T value);
	}
}
