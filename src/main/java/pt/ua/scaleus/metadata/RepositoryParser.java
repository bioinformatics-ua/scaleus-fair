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
// import org.eclipse.rdf4j.model.ValueFactory;
// import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import com.google.common.base.Preconditions;

public class RepositoryParser extends MetadataParser<Repository> {

	@Override
	protected Repository createMetadata() {
		return new Repository();
	}

	// Repository from RDF statements
	@Override
	public Repository parse(List<Statement> statements, IRI fdpURI) {
		Preconditions.checkNotNull(fdpURI, "FDP URI must not be null.");
		Preconditions.checkNotNull(statements, "FDP statements must not be null.");
		Repository metadata = super.parse(statements, fdpURI);
		List<IRI> catalogs = new ArrayList<IRI>();
		// ValueFactory f = SimpleValueFactory.getInstance();
		for (Statement st : statements) {
			Resource subject = st.getSubject();
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (subject.equals(fdpURI)) {
				if (predicate.equals(R3D.DATA_CATALOG)) {
					catalogs.add((IRI) object);
				} else if (predicate.equals(R3D.INSTITUTION)) {
					metadata.setInstitution(AgentParser.parse(statements, (IRI) object));
				}
			}
		}
		if (!catalogs.isEmpty()) {
			metadata.setCatalogs(catalogs);
		}
		return metadata;
	}

	// Repository from RDF string
	public Repository parse(@Nonnull String fdpMetadata, IRI baseURI, @Nonnull RDFFormat format) throws Exception {
		Preconditions.checkNotNull(fdpMetadata, "FDP metadata string must not be null.");
		Preconditions.checkNotNull(format, "RDF format must not be null.");
		Preconditions.checkArgument(!fdpMetadata.isEmpty(), "The fdp metadata content can't be EMPTY");
		List<Statement> statements = Utils.getStatements(fdpMetadata, baseURI, format);
		IRI fdpURI = (IRI) statements.get(0).getSubject();
		Repository metadata = this.parse(statements, fdpURI);
		metadata.setUri(null);
		return metadata;
	}
}
