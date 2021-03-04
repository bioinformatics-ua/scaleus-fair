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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import com.google.common.base.Preconditions;

public class FairMetadataServiceImpl implements FairMetadataService {
	private StoreManager storeManager = new StoreManagerImpl();

	@Override
	public Repository retrieveRepositoryMetadata(@Nonnull IRI uri) throws Exception, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		RepositoryParser parser = Utils.getFdpParser();
		Repository metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public Catalog retrieveCatalogMetadata(@Nonnull IRI uri) throws Exception, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		CatalogParser parser = Utils.getCatalogParser();
		Catalog metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public Dataset retrieveDatasetMetadata(@Nonnull IRI uri) throws Exception, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		DatasetParser parser = Utils.getDatasetParser();
		Dataset metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public Distribution retrieveDistributionMetadata(@Nonnull IRI uri) throws Exception, ResourceNotFoundException {
		List<Statement> statements = retrieveStatements(uri);
		DistributionParser parser = Utils.getDistributionParser();
		Distribution metadata = parser.parse(statements, uri);
		return metadata;
	}

	@Override
	public void storeRepositoryMetadata(@Nonnull Repository metadata) throws Exception {
		Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
		storeMetadata(metadata);
	}

	@Override
	public void storeCatalogMetadata(@Nonnull Catalog metadata) throws Exception {
		Preconditions.checkState(metadata.getParentURI() != null,
				"No fdp URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		storeMetadata(metadata);
	}

	@Override
	public void storeDatasetMetadata(@Nonnull Dataset metadata) throws Exception {
		Preconditions.checkState(metadata.getParentURI() != null,
				"No catalog URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
				"The catalogy URI doesn't exist in the repository. " + "Please try with valid catalogy URI");
		storeMetadata(metadata);
	}

	@Override
	public void storeDistributionMetadata(@Nonnull Distribution metadata) throws Exception {
		Preconditions.checkState(metadata.getParentURI() != null,
				"No dataset URI is provied. Include dcterms:isPartOf statement " + "in the post body rdf");
		Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
				"The dataset URI doesn't exist in the repository. " + "Please try with valid dataset URI");
		storeMetadata(metadata);
	}

	private <T extends Metadata> void storeMetadata(@Nonnull T metadata) throws Exception {
		Preconditions.checkNotNull(metadata, "Metadata must not be null.");
		Preconditions.checkState(!isSubjectURIExist(metadata.getUri()),
				"The metadata URI already exist in the repository. " + "Please try with different ID");
		try {
			if (metadata instanceof Repository) {
				if (metadata.getIssued() == null) {
					metadata.setIssued(Utils.getCurrentTime());
				}
			} else {
				metadata.setIssued(Utils.getCurrentTime());
			}
			metadata.setModified(Utils.getCurrentTime());
			storeManager.storeStatements(Utils.getStatements(metadata));
			updateParentResource(metadata);
		} catch (Exception ex) {
			throw (new Exception(ex.getMessage()));
		}
	}

	private <T extends Metadata> void updateParentResource(@Nonnull T metadata) {
		Preconditions.checkNotNull(metadata, "Metadata object must not be null.");
		try {
			ValueFactory f = SimpleValueFactory.getInstance();
			List<Statement> stmts = new ArrayList<>();
			if (metadata instanceof Repository) {
				return;
			} else if (metadata instanceof Catalog) {
				stmts.add(f.createStatement(metadata.getParentURI(), R3D.DATA_CATALOG, metadata.getUri()));
			} else if (metadata instanceof Dataset) {
				stmts.add(f.createStatement(metadata.getParentURI(), DCAT.DATASET, metadata.getUri()));
			} else if (metadata instanceof Distribution) {
				stmts.add(f.createStatement(metadata.getParentURI(), DCAT.DISTRIBUTION, metadata.getUri()));
			}
			storeManager.removeStatement(metadata.getParentURI(), DCTERMS.MODIFIED, null);
			stmts.add(f.createStatement(metadata.getParentURI(), DCTERMS.MODIFIED, Utils.getCurrentTime()));
			storeManager.storeStatements(stmts);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private List<Statement> retrieveStatements(@Nonnull IRI uri) throws Exception, ResourceNotFoundException {
		try {
			Preconditions.checkNotNull(uri, "Resource uri not be null.");
			List<Statement> statements = storeManager.retrieveResource(uri);
			if (statements.isEmpty()) {
				String msg = ("No metadata found for the uri : " + uri);
				throw (new ResourceNotFoundException(msg));
			}
			addAddtionalResource(statements);
			return statements;
		} catch (Exception ex) {
			throw (new Exception(ex.getMessage()));
		}
	}

	private boolean isSubjectURIExist(@Nonnull IRI uri) throws Exception {
		boolean isURIExist = false;
		try {
			isURIExist = storeManager.isStatementExist(uri, null, null);
		} catch (Exception ex) {
			throw (new Exception(ex.getMessage()));
		}
		return isURIExist;
	}

	private void addAddtionalResource(List<Statement> statements) throws Exception {
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
	public void updateRepositoryMetadata(IRI uri, Repository metaDataUpdate) throws Exception {
		Repository metadata = retrieveRepositoryMetadata(uri);
		setMetadataProperty(metaDataUpdate::getDescription, metadata::setDescription);
		setMetadataProperty(metaDataUpdate::getIdentifier, metadata::setIdentifier);
		setMetadataProperty(metaDataUpdate::getInstitution, metadata::setInstitution);
		setMetadataProperty(metaDataUpdate::getLanguage, metadata::setLanguage);
		setMetadataProperty(metaDataUpdate::getLicense, metadata::setLicense);
		setMetadataProperty(metaDataUpdate::getPublisher, metadata::setPublisher);
		setMetadataProperty(metaDataUpdate::getRights, metadata::setRights);
		setMetadataProperty(metaDataUpdate::getTitle, metadata::setTitle);
		setMetadataProperty(metaDataUpdate::getVersion, metadata::setVersion);
		try {
			storeManager.removeResource(uri);
			storeRepositoryMetadata(metadata);
		} catch (Exception ex) {
			throw (new Exception(ex.getMessage()));
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
