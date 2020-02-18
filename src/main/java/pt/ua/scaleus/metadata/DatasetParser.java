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
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.google.common.base.Preconditions;

public class DatasetParser extends MetadataParser<Dataset> {

	@Override
	protected Dataset createMetadata() {
		return new Dataset();
	}

	// Dataset from RDF statements
	@Override
	public Dataset parse(@Nonnull List<Statement> statements, @Nonnull IRI datasetURI) {
		Preconditions.checkNotNull(datasetURI, "Dataset URI must not be null.");
		Preconditions.checkNotNull(statements, "Dataset statements must not be null.");
		Dataset metadata = super.parse(statements, datasetURI);
		List<IRI> distributions = new ArrayList<IRI>();
		ValueFactory f = SimpleValueFactory.getInstance();
		for (Statement st : statements) {
			Resource subject = st.getSubject();
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (subject.equals(datasetURI)) {
				if (predicate.equals(DCAT.DISTRIBUTION)) {
					distributions.add((IRI) object);
				}
			}
		}
		metadata.setDistributions(distributions);
		return metadata;
	}

	// Dataset from RDF string
	public Dataset parse(@Nonnull String datasetMetadata, @Nonnull IRI datasetURI, IRI catalogURI,
			@Nonnull RDFFormat format) throws Exception {
		Preconditions.checkNotNull(datasetMetadata, "Dataset metadata string must not be null.");
		Preconditions.checkNotNull(datasetURI, "Dataset URI must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!datasetMetadata.isEmpty(), "The dataset metadata content can't be EMPTY");
		List<Statement> statements = Utils.getStatements(datasetMetadata, datasetURI, format);
		Dataset metadata = this.parse(statements, datasetURI);
		metadata.setParentURI(catalogURI);
		return metadata;
	}

	// Dataset from RDF string
	public Dataset parse(@Nonnull String datasetMetadata, IRI baseURI, @Nonnull RDFFormat format) throws Exception {
		Preconditions.checkNotNull(datasetMetadata, "Dataset metadata string must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!datasetMetadata.isEmpty(), "The dataset metadata content can't be EMPTY");
		java.util.List<Statement> statements = Utils.getStatements(datasetMetadata, baseURI, format);
		IRI datasetURI = (IRI) statements.get(0).getSubject();
		Dataset metadata = this.parse(statements, datasetURI);
		metadata.setUri(null);
		return metadata;
	}
}
