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

package pt.ua.fairdata.fairdatapoint.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.google.common.base.Preconditions;
import pt.ua.fairdata.fairdatapoint.repository.StoreManager;
import pt.ua.fairdata.fairdatapoint.repository.StoreManagerException;
import pt.ua.fairdata.fairdatapoint.repository.StoreManagerImpl;
import pt.ua.fairdata.fairmetadata4j.io.CatalogMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.DatasetMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.DistributionMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.FDPMetadataParser;
import pt.ua.fairdata.fairmetadata4j.io.MetadataException;
import pt.ua.fairdata.fairmetadata4j.model.CatalogMetadata;
import pt.ua.fairdata.fairmetadata4j.model.DatasetMetadata;
import pt.ua.fairdata.fairmetadata4j.model.DistributionMetadata;
import pt.ua.fairdata.fairmetadata4j.model.FDPMetadata;
import pt.ua.fairdata.fairmetadata4j.model.Metadata;
import pt.ua.fairdata.fairmetadata4j.utils.MetadataParserUtils;
import pt.ua.fairdata.fairmetadata4j.utils.MetadataUtils;
import pt.ua.fairdata.fairmetadata4j.utils.RDFUtils;
import pt.ua.fairdata.fairmetadata4j.utils.vocabulary.DCAT;
import pt.ua.fairdata.fairmetadata4j.utils.vocabulary.FDP;
import pt.ua.fairdata.fairmetadata4j.utils.vocabulary.R3D;

public class FairMetaDataServiceImpl implements FairMetaDataService {
	private static final Logger log = Logger.getLogger(FairMetaDataServiceImpl.class);
	private StoreManager storeManager = new StoreManagerImpl();

	@Override
	public FDPMetadata retrieveFDPMetaData(@Nonnull IRI uri)
			throws FairMetadataServiceException, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
		FDPMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public CatalogMetadata retrieveCatalogMetaData(@Nonnull IRI uri)
			throws FairMetadataServiceException, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		CatalogMetadataParser parser = MetadataParserUtils.getCatalogParser();
		CatalogMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public DatasetMetadata retrieveDatasetMetaData(@Nonnull IRI uri)
			throws FairMetadataServiceException, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		DatasetMetadataParser parser = MetadataParserUtils.getDatasetParser();
		DatasetMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public DistributionMetadata retrieveDistributionMetaData(@Nonnull IRI uri)
			throws FairMetadataServiceException, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		DistributionMetadataParser parser = MetadataParserUtils.getDistributionParser();
		DistributionMetadata metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public void storeFDPMetaData(@Nonnull FDPMetadata metadata) throws FairMetadataServiceException, MetadataException {
		Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
		storeMetadata(metadata);
	}

	@Override
	public void storeCatalogMetaData(@Nonnull CatalogMetadata metadata)
			throws FairMetadataServiceException, MetadataException {
		Preconditions.checkState(metadata.getParentURI() != null,
				"No fdp URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		storeMetadata(metadata);
	}

	@Override
	public void storeDatasetMetaData(@Nonnull DatasetMetadata metadata)
			throws FairMetadataServiceException, MetadataException {
		Preconditions.checkState(metadata.getParentURI() != null,
				"No catalog URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
				"The catalogy URI doesn't exist in the repository. " + "Please try with valid catalogy URI");
		storeMetadata(metadata);
	}

	@Override
	public void storeDistributionMetaData(@Nonnull DistributionMetadata metadata)
			throws FairMetadataServiceException, MetadataException {
		Preconditions.checkState(metadata.getParentURI() != null,
				"No dataset URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
				"The dataset URI doesn't exist in the repository. " + "Please try with valid dataset URI");
		storeMetadata(metadata);
	}

	private <T extends Metadata> void storeMetadata(@Nonnull T metadata)
			throws FairMetadataServiceException, MetadataException {
		Preconditions.checkNotNull(metadata, "Metadata must not be null.");
		Preconditions.checkState(!isSubjectURIExist(metadata.getUri()),
				"The metadata URI already exist in the repository. " + "Please try with different ID");
		try {
			if (metadata instanceof FDPMetadata) {
				if (metadata.getIssued() == null) {
					metadata.setIssued(RDFUtils.getCurrentTime());
				}
			} else {
				metadata.setIssued(RDFUtils.getCurrentTime());
			}
			metadata.setModified(RDFUtils.getCurrentTime());
			storeManager.storeStatements(MetadataUtils.getStatements(metadata));
			updateParentResource(metadata);
		} catch (StoreManagerException | DatatypeConfigurationException ex) {
			log.error("Error storing distribution metadata");
			throw (new FairMetadataServiceException(ex.getMessage()));
		}
	}

	private <T extends Metadata> void updateParentResource(@Nonnull T metadata) {
		Preconditions.checkNotNull(metadata, "Metadata object must not be null.");
		try {
			ValueFactory f = SimpleValueFactory.getInstance();
			List<Statement> stmts = new ArrayList<>();
			if (metadata instanceof FDPMetadata) {
				return;
			} else if (metadata instanceof CatalogMetadata) {
				stmts.add(f.createStatement(metadata.getParentURI(), R3D.DATA_CATALOG, metadata.getUri()));
			} else if (metadata instanceof DatasetMetadata) {
				stmts.add(f.createStatement(metadata.getParentURI(), DCAT.DATASET, metadata.getUri()));
			} else if (metadata instanceof DistributionMetadata) {
				stmts.add(f.createStatement(metadata.getParentURI(), DCAT.DISTRIBUTION, metadata.getUri()));
			}
			storeManager.removeStatement(metadata.getParentURI(), DCTERMS.MODIFIED, null);
			stmts.add(f.createStatement(metadata.getParentURI(), DCTERMS.MODIFIED, RDFUtils.getCurrentTime()));
			storeManager.storeStatements(stmts);
		} catch (StoreManagerException | DatatypeConfigurationException ex) {
			log.error("Error updating parent resource :" + ex.getMessage());
		}
	}

	private List<Statement> retrieveStatements(@Nonnull IRI uri)
			throws FairMetadataServiceException, ResourceNotFoundException {
		try {
			Preconditions.checkNotNull(uri, "Resource uri not be null.");
			List<Statement> statements = storeManager.retrieveResource(uri);
			if (statements.isEmpty()) {
				String msg = ("No metadata found for the uri : " + uri);
				throw (new ResourceNotFoundException(msg));
			}
			addAddtionalResource(statements);
			return statements;
		} catch (StoreManagerException ex) {
			log.error("Error retrieving fdp metadata from the store");
			throw (new FairMetadataServiceException(ex.getMessage()));
		}
	}

	private boolean isSubjectURIExist(@Nonnull IRI uri) throws FairMetadataServiceException {
		boolean isURIExist = false;
		try {
			isURIExist = storeManager.isStatementExist(uri, null, null);
		} catch (StoreManagerException ex) {
			log.error("Error checking existence of subject URI");
			throw (new FairMetadataServiceException(ex.getMessage()));
		}
		return isURIExist;
	}

	private void addAddtionalResource(List<Statement> statements) throws StoreManagerException {
		List<Statement> otherResources = new ArrayList<>();
		for (Statement st : statements) {
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (predicate.equals(FDP.METADATA_IDENTIFIER)) {
				otherResources.addAll(storeManager.retrieveResource((IRI) object));
			} else if (predicate.equals(R3D.INSTITUTION)) {
				otherResources.addAll(storeManager.retrieveResource((IRI) object));
			} else if (predicate.equals(DCTERMS.PUBLISHER)) {
				otherResources.addAll(storeManager.retrieveResource((IRI) object));
			} else if (predicate.equals(R3D.REPO_IDENTIFIER)) {
				otherResources.addAll(storeManager.retrieveResource((IRI) object));
			}
		}
		statements.addAll(otherResources);
	}

	@Override
	public void updateFDPMetaData(IRI uri, FDPMetadata metaDataUpdate)
			throws FairMetadataServiceException, MetadataException {
		FDPMetadata metadata = retrieveFDPMetaData(uri);
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
		} catch (StoreManagerException ex) {
			log.error("Error deleting existence fdp resource");
			throw (new FairMetadataServiceException(ex.getMessage()));
		}
	}

	private <T> void setMetadataProperty(Getter<T> getter, Setter<T> setter) {
		if (getter.get() != null) {
			setter.set(getter.get());
		}
	}

	@FunctionalInterface
	private interface Getter<T> {
		T get();
	}

	@FunctionalInterface
	private interface Setter<T> {
		void set(T value);
	}
}
