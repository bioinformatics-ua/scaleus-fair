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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import com.google.common.base.Preconditions;

public class StoreManagerImpl implements StoreManager {
	private static final Logger log = Logger.getLogger(StoreManagerImpl.class);
	File dataDir = new File("/fairdatadir/repository/");
	private Repository repository = new SailRepository(new NativeStore(dataDir));

	@Override
	public List<Statement> retrieveResource(@Nonnull IRI uri) throws Exception {
		Preconditions.checkNotNull(uri, "URI must not be null.");
		log.info("Get statements for the URI <" + uri.toString() + ">");
		try (RepositoryConnection conn = getRepositoryConnection()) {
			org.eclipse.rdf4j.repository.RepositoryResult<Statement> queryResult = conn.getStatements(uri, null, null,
					false);
			List<Statement> statements = new ArrayList<Statement>();
			while (queryResult.hasNext()) {
				statements.add(queryResult.next());
			}
			return statements;
		} catch (RepositoryException e) {
			throw (new Exception("Error retrieve resource :" + e.getMessage()));
		}
	}

	@Override
	public boolean isStatementExist(Resource rsrc, IRI pred, Value value) throws Exception {
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(rsrc, pred, value, false);
		} catch (org.eclipse.rdf4j.repository.RepositoryException e) {
			throw (new Exception("Error check statement existence :" + e.getMessage()));
		}
	}

	@Override
	public void storeStatements(List<Statement> statements) throws Exception {
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(statements);
		} catch (RepositoryException e) {
			throw (new Exception("Error storing statements :" + e.getMessage()));
		}
	}

	@Override
	public void removeStatement(org.eclipse.rdf4j.model.Resource rsrc, IRI pred, Value value) throws Exception {
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(rsrc, pred, value);
		} catch (RepositoryException e) {
			throw (new Exception("Error removing statement"));
		}
	}

	private RepositoryConnection getRepositoryConnection() throws RepositoryException {
		repository.initialize();
		return this.repository.getConnection();
	}

	@Override
	public void removeResource(IRI uri) throws Exception {
		removeStatement(uri, null, null);
	}
}
