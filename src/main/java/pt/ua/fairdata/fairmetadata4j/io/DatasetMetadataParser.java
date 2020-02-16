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

package pt.ua.fairdata.fairmetadata4j.io;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.google.common.base.Preconditions;
import pt.ua.fairdata.fairmetadata4j.model.DatasetMetadata;
import pt.ua.fairdata.fairmetadata4j.utils.RDFUtils;
import pt.ua.fairdata.fairmetadata4j.utils.vocabulary.DCAT;

// Parser for dataset metadata object
public class DatasetMetadataParser extends MetadataParser<DatasetMetadata> {

	@Override
	protected DatasetMetadata createMetadata() {
		return new DatasetMetadata();
	}

	// Parse RDF statements to create dataset metadata object
	@Override
	public DatasetMetadata parse(@Nonnull List<Statement> statements, @Nonnull IRI datasetURI) {
		Preconditions.checkNotNull(datasetURI, "Dataset URI must not be null.");
		Preconditions.checkNotNull(statements, "Dataset statements must not be null.");
		DatasetMetadata metadata = super.parse(statements, datasetURI);
		List<IRI> distributions = new ArrayList<IRI>();
		ValueFactory f = SimpleValueFactory.getInstance();
		List<Literal> keywords = new ArrayList<Literal>();
		for (Statement st : statements) {
			Resource subject = st.getSubject();
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (subject.equals(datasetURI)) {
				if (predicate.equals(DCAT.LANDING_PAGE)) {
					metadata.setLandingPage((IRI) object);
				} else if (predicate.equals(DCAT.THEME)) {
					metadata.getThemes().add((IRI) object);
				} else if (predicate.equals(DCAT.CONTACT_POINT)) {
					metadata.setContactPoint((IRI) object);
				} else if (predicate.equals(DCAT.KEYWORD)) {
					keywords.add(f.createLiteral(object.stringValue(), XMLSchema.STRING));
				} else if (predicate.equals(DCAT.DISTRIBUTION)) {
					distributions.add((IRI) object);
				} else if (predicate.equals(DCTERMS.ISSUED)) {
					metadata.setDatasetIssued(f.createLiteral(object.stringValue(), XMLSchema.DATETIME));
				} else if (predicate.equals(DCTERMS.MODIFIED)) {
					metadata.setDatasetModified(f.createLiteral(object.stringValue(), XMLSchema.DATETIME));
				}
			}
		}
		metadata.setKeywords(keywords);
		metadata.setDistributions(distributions);
		return metadata;
	}

	// Parse RDF string to create dataset metadata object
	public DatasetMetadata parse(@Nonnull String datasetMetadata, @Nonnull IRI datasetURI, IRI catalogURI,
			@Nonnull RDFFormat format) throws MetadataParserException {
		Preconditions.checkNotNull(datasetMetadata, "Dataset metadata string must not be null.");
		Preconditions.checkNotNull(datasetURI, "Dataset URI must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!datasetMetadata.isEmpty(), "The dataset metadata content can't be EMPTY");
		List<Statement> statements = RDFUtils.getStatements(datasetMetadata, datasetURI, format);
		DatasetMetadata metadata = this.parse(statements, datasetURI);
		metadata.setParentURI(catalogURI);
		return metadata;
	}

	// Parse RDF string to create dataset metadata object
	public DatasetMetadata parse(@Nonnull String datasetMetadata, IRI baseURI, @Nonnull RDFFormat format)
			throws MetadataParserException {
		Preconditions.checkNotNull(datasetMetadata, "Dataset metadata string must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!datasetMetadata.isEmpty(), "The dataset metadata content can't be EMPTY");
		java.util.List<Statement> statements = RDFUtils.getStatements(datasetMetadata, baseURI, format);
		IRI datasetURI = (IRI) statements.get(0).getSubject();
		DatasetMetadata metadata = this.parse(statements, datasetURI);
		metadata.setUri(null);
		return metadata;
	}
}
