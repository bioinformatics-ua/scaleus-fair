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
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

public abstract class MetadataParser<T extends Metadata> {

	protected abstract T createMetadata();

	// Parse common metadata properties
	protected T parse(List<Statement> statements, IRI metadataUri) {
		T metadata = createMetadata();
		metadata.setUri(metadataUri);
		ValueFactory f = SimpleValueFactory.getInstance();
		for (Statement st : statements) {
			Resource subject = st.getSubject();
			IRI predicate = st.getPredicate();
			Value object = st.getObject();
			if (subject.equals(metadataUri)) {
				if (predicate.equals(DCTERMS.HAS_VERSION)) {
					metadata.setVersion(f.createLiteral(object.stringValue(), XMLSchema.STRING));
				} else if (predicate.equals(RDFS.LABEL) || predicate.equals(DCTERMS.TITLE)) {
					metadata.setTitle(f.createLiteral(object.stringValue(), XMLSchema.STRING));
				} else if (predicate.equals(DCTERMS.DESCRIPTION)) {
					metadata.setDescription(f.createLiteral(object.stringValue(), XMLSchema.STRING));
				} else if (predicate.equals(DCTERMS.LICENSE)) {
					metadata.setLicense((IRI) object);
				} else if (predicate.equals(DCTERMS.RIGHTS)) {
					metadata.setRights((IRI) object);
				} else if (predicate.equals(DCTERMS.LANGUAGE)) {
					metadata.setLanguage((IRI) object);
				} else if (predicate.equals(DCTERMS.PUBLISHER)) {
					metadata.setPublisher(AgentParser.parse(statements, (IRI) object));
				} else if (predicate.equals(FDP.METADATA_IDENTIFIER)) {
					metadata.setIdentifier(IdentifierParser.parse(statements, (IRI) object));
				} else if (predicate.equals(FDP.METADATA_ISSUED) && metadata.getIssued() == null) {
					metadata.setIssued(f.createLiteral(object.stringValue(), XMLSchema.DATETIME));
				} else if (predicate.equals(FDP.METADATA_MODIFIED) && metadata.getModified() == null) {
					metadata.setModified(f.createLiteral(object.stringValue(), XMLSchema.DATETIME));
				} else if (predicate.equals(DCTERMS.IS_PART_OF)) {
					metadata.setParentURI((IRI) object);
				}
			}
		}
		return metadata;
	}
}
