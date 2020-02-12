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

package pt.ua.fairdata.fairdatapoint.service;

/**
 * Fair metadata service interface
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2015-11-23
 * @version 0.2
 */
public interface FairMetaDataService {

	/**
	 * Get metadata of given fdp URI
	 * 
	 * @param uri fdp URI
	 * @return FDPMetadata object
	 * @throws FairMetadataServiceException
	 */
	nl.dtl.fairmetadata4j.model.FDPMetadata retrieveFDPMetaData(org.eclipse.rdf4j.model.IRI uri)
			throws FairMetadataServiceException;

	/**
	 * Get metadata of given catalog URI
	 * 
	 * @param uri catalog URI
	 * @return CatalogMetadata object
	 * @throws FairMetadataServiceException
	 */
	nl.dtl.fairmetadata4j.model.CatalogMetadata retrieveCatalogMetaData(org.eclipse.rdf4j.model.IRI uri)
			throws FairMetadataServiceException;

	/**
	 * Get metadata of given dataset URI
	 * 
	 * @param uri dataset URI
	 * @return DatasetMetadata object
	 * @throws FairMetadataServiceException
	 */
	nl.dtl.fairmetadata4j.model.DatasetMetadata retrieveDatasetMetaData(org.eclipse.rdf4j.model.IRI uri)
			throws FairMetadataServiceException;

	/**
	 * Get metadata of given distribution URI
	 * 
	 * @param uri distribution URI
	 * @return DistributionMetadata object
	 * @throws FairMetadataServiceException
	 */
	nl.dtl.fairmetadata4j.model.DistributionMetadata retrieveDistributionMetaData(
			org.eclipse.rdf4j.model.IRI uri) throws FairMetadataServiceException;

	/**
	 * Store catalog metadata
	 * 
	 * @param catalogMetadata
	 * @throws FairMetadataServiceException
	 * @throws                              nl.dtl.fairmetadata4j.io.MetadataException
	 */
	void storeCatalogMetaData(nl.dtl.fairmetadata4j.model.CatalogMetadata catalogMetadata)
			throws FairMetadataServiceException, nl.dtl.fairmetadata4j.io.MetadataException;

	/**
	 * Store dataset metadata
	 * 
	 * @param datasetMetadata
	 * @throws FairMetadataServiceException
	 * @throws                              nl.dtl.fairmetadata4j.io.MetadataException
	 */
	void storeDatasetMetaData(nl.dtl.fairmetadata4j.model.DatasetMetadata datasetMetadata)
			throws FairMetadataServiceException, nl.dtl.fairmetadata4j.io.MetadataException;

	/**
	 * Store fdp metadata
	 * 
	 * @param fdpMetaData
	 * @throws FairMetadataServiceException
	 * @throws                              nl.dtl.fairmetadata4j.io.MetadataException
	 */
	void storeFDPMetaData(nl.dtl.fairmetadata4j.model.FDPMetadata fdpMetaData)
			throws FairMetadataServiceException, nl.dtl.fairmetadata4j.io.MetadataException;

	/**
	 * Store distribution metadata
	 * 
	 * @param distributionMetadata
	 * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
	 * @throws nl.dtl.fairmetadata4j.io.MetadataException
	 */
	void storeDistributionMetaData(nl.dtl.fairmetadata4j.model.DistributionMetadata distributionMetadata)
			throws FairMetadataServiceException, nl.dtl.fairmetadata4j.io.MetadataException;

	/**
	 * Update fdp metadata
	 * 
	 * @param uri
	 * @param fdpMetaData
	 * @throws FairMetadataServiceException
	 * @throws                              nl.dtl.fairmetadata4j.io.MetadataException
	 */
	void updateFDPMetaData(org.eclipse.rdf4j.model.IRI uri, nl.dtl.fairmetadata4j.model.FDPMetadata fdpMetaData)
			throws FairMetadataServiceException, nl.dtl.fairmetadata4j.io.MetadataException;
}
