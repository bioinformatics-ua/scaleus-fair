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

import java.util.List;
import javax.annotation.Nonnull;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.google.common.base.Preconditions;

public class DistributionParser extends MetadataParser<Distribution> {

	@Override
	protected Distribution createMetadata() {
		return new Distribution();
	}

	// Distribution from RDF statements
	@Override
	public Distribution parse(@Nonnull List<Statement> statements, @Nonnull IRI distributionURI) {
		Preconditions.checkNotNull(distributionURI, "Distribution URI must not be null.");
		Preconditions.checkNotNull(statements, "Distribution statements must not be null.");
		Distribution metadata = super.parse(statements, distributionURI);
		ValueFactory f = SimpleValueFactory.getInstance();
		for (Statement st : statements) {
			Resource subject = st.getSubject();
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (subject.equals(distributionURI)) {
				if (predicate.equals(DCAT.DOWNLOAD_URL)) {
					metadata.setDownloadURL((IRI) object);
				} else if (predicate.equals(DCAT.FORMAT)) {
					metadata.setFormat(f.createLiteral(object.stringValue(), XMLSchema.STRING));
				}
			}
		}
		return metadata;
	}

	// Distribution from RDF string
	public Distribution parse(@Nonnull String distributionMetadata, @Nonnull IRI distributionURI, IRI datasetURI,
			@Nonnull RDFFormat format) throws Exception {
		Preconditions.checkNotNull(distributionMetadata, "Distribution metadata string must not be null.");
		Preconditions.checkNotNull(distributionURI, "Distribution URI must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!distributionMetadata.isEmpty(),
				"The distribution metadata content can't be EMPTY");
		List<Statement> statements = Utils.getStatements(distributionMetadata, distributionURI, format);
		Distribution metadata = this.parse(statements, distributionURI);
		metadata.setParentURI(datasetURI);
		return metadata;
	}

	// Distribution from RDF string
	public Distribution parse(@Nonnull String distributionMetadata, IRI baseURI, @Nonnull RDFFormat format)
			throws Exception {
		Preconditions.checkNotNull(distributionMetadata, "Distribution metadata string must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!distributionMetadata.isEmpty(),
				"The distribution metadata content can't be EMPTY");
		List<Statement> statements = Utils.getStatements(distributionMetadata, baseURI, format);
		IRI catalogURI = (IRI) statements.get(0).getSubject();
		Distribution metadata = this.parse(statements, catalogURI);
		metadata.setUri(null);
		return metadata;
	}
}
