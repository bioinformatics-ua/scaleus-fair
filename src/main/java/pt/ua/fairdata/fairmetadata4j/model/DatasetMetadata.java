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

// Dataset metadata object
public final class DatasetMetadata extends Metadata {
	private List<IRI> distributions = new ArrayList<IRI>();
	private List<IRI> themes = new ArrayList<IRI>();
	private IRI contactPoint;
	private List<Literal> keywords = new ArrayList<Literal>();
	private IRI landingPage;
	private Literal datasetIssued;
	private Literal datasetModified;

	public void setDistributions(List<IRI> distribution) {
		this.distributions = distribution;
	}

	public void setThemes(List<IRI> themes) {
		this.themes = themes;
	}

	public void setContactPoint(IRI contactPoint) {
		this.contactPoint = contactPoint;
	}

	public void setKeywords(List<Literal> keywords) {
		this.keywords = keywords;
	}

	public void setLandingPage(IRI landingPage) {
		this.landingPage = landingPage;
	}

	public List<IRI> getDistributions() {
		return distributions;
	}

	public List<IRI> getThemes() {
		return themes;
	}

	public IRI getContactPoint() {
		return contactPoint;
	}

	public List<Literal> getKeywords() {
		return keywords;
	}

	public IRI getLandingPage() {
		return landingPage;
	}

	public Literal getDatasetIssued() {
		return datasetIssued;
	}

	public void setDatasetIssued(Literal datasetIssued) {
		this.datasetIssued = datasetIssued;
	}

	public Literal getDatasetModified() {
		return datasetModified;
	}

	public void setDatasetModified(Literal datasetModified) {
		this.datasetModified = datasetModified;
	}
}
