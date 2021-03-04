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
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.google.common.base.Preconditions;

public class CatalogParser extends MetadataParser<Catalog> {

	@Override
	protected Catalog createMetadata() {
		return new Catalog();
	}

	// Catalog from RDF statements
	@Override
	public Catalog parse(@Nonnull List<Statement> statements, @Nonnull IRI catalogURI) {
		Preconditions.checkNotNull(catalogURI, "Catalog URI must not be null.");
		Preconditions.checkNotNull(statements, "Catalog statements must not be null.");
		Catalog metadata = super.parse(statements, catalogURI);
		List<IRI> datasets = new ArrayList<IRI>();
		ValueFactory f = SimpleValueFactory.getInstance();
		for (Statement st : statements) {
			Resource subject = st.getSubject();
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (subject.equals(catalogURI)) {
				if (predicate.equals(FOAF.HOMEPAGE)) {
					metadata.setHomepage((IRI) object);
				} else if (predicate.equals(DCAT.DATASET)) {
					datasets.add((IRI) object);
				}
			}
		}
		if (!datasets.isEmpty()) {
			metadata.setDatasets(datasets);
		}
		return metadata;
	}

	// Catalog from RDF string
	public Catalog parse(@Nonnull String catalogMetadata, @Nonnull IRI catalogURI, IRI fdpURI,
			@Nonnull RDFFormat format) throws Exception {
		Preconditions.checkNotNull(catalogMetadata, "Catalog metadata string must not be null.");
		Preconditions.checkNotNull(catalogURI, "Catalog URI must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!catalogMetadata.isEmpty(), "The catalog metadata content can't be EMPTY");
		List<Statement> statements = Utils.getStatements(catalogMetadata, catalogURI, format);
		Catalog metadata = this.parse(statements, catalogURI);
		metadata.setParentURI(fdpURI);
		return metadata;
	}

	// Catalog from RDF string
	public Catalog parse(@Nonnull String catalogMetadata, IRI baseURI, @Nonnull RDFFormat format) throws Exception {
		Preconditions.checkNotNull(catalogMetadata, "Catalog metadata string must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!catalogMetadata.isEmpty(), "The catalog metadata content can't be EMPTY");
		List<Statement> statements = Utils.getStatements(catalogMetadata, baseURI, format);
		IRI catalogURI = (IRI) statements.get(0).getSubject();
		Catalog metadata = this.parse(statements, catalogURI);
		metadata.setUri(null);
		return metadata;
	}
}
