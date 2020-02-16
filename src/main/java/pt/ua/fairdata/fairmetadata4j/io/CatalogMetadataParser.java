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
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.google.common.base.Preconditions;
import pt.ua.fairdata.fairmetadata4j.model.CatalogMetadata;
import pt.ua.fairdata.fairmetadata4j.utils.RDFUtils;
import pt.ua.fairdata.fairmetadata4j.utils.vocabulary.DCAT;

// Parser for catalog metadata object
public class CatalogMetadataParser extends MetadataParser<CatalogMetadata> {

	@Override
	protected CatalogMetadata createMetadata() {
		return new CatalogMetadata();
	}

	// Parse RDF statements to create catalog metadata object
	@Override
	public CatalogMetadata parse(@Nonnull List<Statement> statements, @Nonnull IRI catalogURI) {
		Preconditions.checkNotNull(catalogURI, "Catalog URI must not be null.");
		Preconditions.checkNotNull(statements, "Catalog statements must not be null.");
		CatalogMetadata metadata = super.parse(statements, catalogURI);
		List<IRI> datasets = new ArrayList<IRI>();
		ValueFactory f = SimpleValueFactory.getInstance();
		for (Statement st : statements) {
			Resource subject = st.getSubject();
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (subject.equals(catalogURI)) {
				if (predicate.equals(FOAF.HOMEPAGE)) {
					metadata.setHomepage((IRI) object);
				} else if (predicate.equals(DCAT.THEME_TAXONOMY)) {
					metadata.getThemeTaxonomys().add((IRI) object);
				} else if (predicate.equals(DCAT.DATASET)) {
					datasets.add((IRI) object);
				} else if (predicate.equals(DCTERMS.ISSUED)) {
					metadata.setCatalogIssued(f.createLiteral(object.stringValue(), XMLSchema.DATETIME));
				} else if (predicate.equals(DCTERMS.MODIFIED)) {
					metadata.setCatalogModified(f.createLiteral(object.stringValue(), XMLSchema.DATETIME));
				}
			}
		}
		if (!datasets.isEmpty()) {
			metadata.setDatasets(datasets);
		}
		return metadata;
	}

	/**
	 * Parse RDF string to create catalog metadata object
	 *
	 * @param catalogMetadata Catalog metadata as a RDF string
	 * @param catalogURI      Catalog URI
	 * @param fdpURI          FairDataPoint URI
	 * @param format          RDF string's RDF format
	 * @return CatalogMetadata object
	 * @throws MetadataParserException
	 */
	public CatalogMetadata parse(@javax.annotation.Nonnull String catalogMetadata, @Nonnull IRI catalogURI, IRI fdpURI,
			@Nonnull RDFFormat format) throws MetadataParserException {
		Preconditions.checkNotNull(catalogMetadata, "Catalog metadata string must not be null.");
		Preconditions.checkNotNull(catalogURI, "Catalog URI must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!catalogMetadata.isEmpty(), "The catalog metadata content can't be EMPTY");
		List<Statement> statements = RDFUtils.getStatements(catalogMetadata, catalogURI, format);
		CatalogMetadata metadata = this.parse(statements, catalogURI);
		metadata.setParentURI(fdpURI);
		return metadata;
	}

	/**
	 * Parse RDF string to create catalog metadata object
	 *
	 * @param catalogMetadata Catalog metadata as a RDF string
	 * @param baseURI
	 * @param format          RDF string's RDF format
	 * @return CatalogMetadata object
	 * @throws MetadataParserException
	 */
	public CatalogMetadata parse(@Nonnull String catalogMetadata, IRI baseURI, @Nonnull RDFFormat format)
			throws MetadataParserException {
		Preconditions.checkNotNull(catalogMetadata, "Catalog metadata string must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!catalogMetadata.isEmpty(), "The catalog metadata content can't be EMPTY");
		List<Statement> statements = RDFUtils.getStatements(catalogMetadata, baseURI, format);
		IRI catalogURI = (IRI) statements.get(0).getSubject();
		CatalogMetadata metadata = this.parse(statements, catalogURI);
		metadata.setUri(null);
		return metadata;
	}
}
