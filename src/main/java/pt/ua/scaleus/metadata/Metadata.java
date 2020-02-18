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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

public class Metadata {

	private Literal title;
	private Identifier identifier;
	private Literal issued;
	private Literal modified;
	private Literal version;
	private Literal description;
	private IRI license;
	private IRI rights;
	private IRI uri;
	private IRI parentURI;
	private IRI language;
	private Agent publisher;

	public void setTitle(Literal title) {
		this.title = title;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setIssued(Literal issued) {
		this.issued = issued;
	}

	public void setModified(Literal modified) {
		this.modified = modified;
	}

	public void setVersion(Literal version) {
		this.version = version;
	}

	public void setDescription(Literal description) {
		this.description = description;
	}

	public void setLicense(IRI license) {
		this.license = license;
	}

	public void setRights(IRI rights) {
		this.rights = rights;
	}

	public void setUri(IRI uri) {
		this.uri = uri;
	}

	public Literal getTitle() {
		return title;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public Literal getIssued() {
		return issued;
	}

	public Literal getModified() {
		return modified;
	}

	public Literal getVersion() {
		return version;
	}

	public Literal getDescription() {
		return description;
	}

	public IRI getLicense() {
		return license;
	}

	public IRI getRights() {
		return rights;
	}

	public IRI getUri() {
		return uri;
	}

	public Agent getPublisher() {
		return publisher;
	}

	public IRI getLanguage() {
		return language;
	}

	public void setLanguage(IRI language) {
		this.language = language;
	}

	public IRI getParentURI() {
		return parentURI;
	}

	public void setParentURI(IRI parentURI) {
		this.parentURI = parentURI;
	}

	public void setPublisher(Agent publisher) {
		this.publisher = publisher;
	}
}
