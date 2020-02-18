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
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

// DCAT vocabulary. See https://www.w3.org/TR/vocab-dcat/
public class DCAT {
	private static final ValueFactory f = SimpleValueFactory.getInstance();
	public static final String PREFIX = "dcat";
	public static final String NAMESPACE = "http://www.w3.org/ns/dcat#";
	public static final IRI TYPE_CATALOG = f.createIRI(NAMESPACE + "Catalog");
	public static final IRI TYPE_DATASET = f.createIRI(NAMESPACE + "Dataset");
	public static final IRI TYPE_DISTRIBUTION = f.createIRI(NAMESPACE + "Distribution");
	public static final IRI DATASET = f.createIRI(NAMESPACE + "dataset");
	public static final IRI DISTRIBUTION = f.createIRI(NAMESPACE + "distribution");
	public static final IRI DOWNLOAD_URL = f.createIRI(NAMESPACE + "downloadURL");
	public static final IRI FORMAT = f.createIRI(NAMESPACE + "format");
}
