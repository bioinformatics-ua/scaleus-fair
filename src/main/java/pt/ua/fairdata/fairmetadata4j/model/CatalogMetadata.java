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

package pt.ua.fairdata.fairmetadata4j.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;

// Catalog metadata object
public final class CatalogMetadata extends Metadata {

	private IRI homepage;
	private List<IRI> datasets = new ArrayList<IRI>();
	private List<IRI> themeTaxonomys = new ArrayList<IRI>();
	private Literal catalogIssued;
	private Literal catalogModified;

	/**
	 * @param homepage the homepage to set
	 */
	public void setHomepage(IRI homepage) {
		this.homepage = homepage;
	}

	public void setDatasets(List<IRI> datasets) {
		this.datasets = datasets;
	}

	public void setThemeTaxonomys(List<IRI> themeTaxonomys) {
		this.themeTaxonomys = themeTaxonomys;
	}

	public IRI getHomepage() {
		return homepage;
	}

	public List<IRI> getDatasets() {
		return datasets;
	}

	public List<IRI> getThemeTaxonomys() {
		return themeTaxonomys;
	}

	public Literal getCatalogIssued() {
		return catalogIssued;
	}

	public void setCatalogIssued(Literal catalogIssued) {
		this.catalogIssued = catalogIssued;
	}

	public Literal getCatalogModified() {
		return catalogModified;
	}

	public void setCatalogModified(Literal catalogModified) {
		this.catalogModified = catalogModified;
	}
}
