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

package pt.ua.fairdata.fairdatapoint.repository.impl;

/**
 * Contain methods to store data and retrieve data from the triple store
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-01-05
 * @version 0.2
 */

public class StoreManagerImpl implements pt.ua.fairdata.fairdatapoint.repository.StoreManager {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StoreManagerImpl.class);
	// private org.eclipse.rdf4j.repository.Repository repository = new
	// org.eclipse.rdf4j.repository.sail.SailRepository(
	// new org.eclipse.rdf4j.sail.memory.MemoryStore());
	java.io.File dataDir = new java.io.File("/fairdatadir/repository/");
	private org.eclipse.rdf4j.repository.Repository repository = new org.eclipse.rdf4j.repository.sail.SailRepository(
			new org.eclipse.rdf4j.sail.nativerdf.NativeStore(dataDir));

	/**
	 * Retrieve all statements for an given URI
	 *
	 * @param uri Valid RDF URI as a string
	 * @return List of RDF statements
	 * @throws StoreManagerException
	 */
	@Override
	public java.util.List<org.eclipse.rdf4j.model.Statement> retrieveResource(
			@javax.annotation.Nonnull org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.repository.StoreManagerException {
		com.google.common.base.Preconditions.checkNotNull(uri, "URI must not be null.");
		log.info("Get statements for the URI <" + uri.toString() + ">");
		try (org.eclipse.rdf4j.repository.RepositoryConnection conn = getRepositoryConnection()) {
			org.eclipse.rdf4j.repository.RepositoryResult<org.eclipse.rdf4j.model.Statement> queryResult = conn
					.getStatements(uri, null, null, false);
			java.util.List<org.eclipse.rdf4j.model.Statement> statements = new java.util.ArrayList<org.eclipse.rdf4j.model.Statement>();
			while (queryResult.hasNext()) {
				statements.add(queryResult.next());
			}
			return statements;
		} catch (org.eclipse.rdf4j.repository.RepositoryException e) {
			throw (new pt.ua.fairdata.fairdatapoint.repository.StoreManagerException(
					"Error retrieve resource :" + e.getMessage()));
		}
	}

	/**
	 * Check if a statement exist in a triple store
	 *
	 * @param rsrc
	 * @param pred
	 * @param value
	 * @return
	 * @throws StoreManagerException
	 */
	@Override
	public boolean isStatementExist(org.eclipse.rdf4j.model.Resource rsrc, org.eclipse.rdf4j.model.IRI pred,
			org.eclipse.rdf4j.model.Value value) throws pt.ua.fairdata.fairdatapoint.repository.StoreManagerException {
		try (org.eclipse.rdf4j.repository.RepositoryConnection conn = getRepositoryConnection()) {
			log.info("Check if statements exists");
			return conn.hasStatement(rsrc, pred, value, false);
		} catch (org.eclipse.rdf4j.repository.RepositoryException e) {
			throw (new pt.ua.fairdata.fairdatapoint.repository.StoreManagerException(
					"Error check statement existence :" + e.getMessage()));
		}
	}

	/**
	 * Store string RDF to the repository
	 *
	 */
	@Override
	public void storeStatements(java.util.List<org.eclipse.rdf4j.model.Statement> statements)
			throws pt.ua.fairdata.fairdatapoint.repository.StoreManagerException {
		try (org.eclipse.rdf4j.repository.RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(statements);
		} catch (org.eclipse.rdf4j.repository.RepositoryException e) {
			throw (new pt.ua.fairdata.fairdatapoint.repository.StoreManagerException(
					"Error storing statements :" + e.getMessage()));
		}
	}

	/**
	 * Remove a statement from the repository
	 *
	 * @param pred
	 */
	@Override
	public void removeStatement(org.eclipse.rdf4j.model.Resource rsrc, org.eclipse.rdf4j.model.IRI pred,
			org.eclipse.rdf4j.model.Value value) throws pt.ua.fairdata.fairdatapoint.repository.StoreManagerException {
		try (org.eclipse.rdf4j.repository.RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(rsrc, pred, value);
		} catch (org.eclipse.rdf4j.repository.RepositoryException e) {
			throw (new pt.ua.fairdata.fairdatapoint.repository.StoreManagerException("Error removing statement"));
		}
	}

	/**
	 * Repository connection to interact with the triple store
	 *
	 * @return RepositoryConnection
	 * @throws Exception
	 */
	private org.eclipse.rdf4j.repository.RepositoryConnection getRepositoryConnection()
			throws org.eclipse.rdf4j.repository.RepositoryException {
		repository.initialize();
		return this.repository.getConnection();
	}

	@Override
	public void removeResource(org.eclipse.rdf4j.model.IRI uri)
			throws pt.ua.fairdata.fairdatapoint.repository.StoreManagerException {
		removeStatement(uri, null, null);
	}
}
