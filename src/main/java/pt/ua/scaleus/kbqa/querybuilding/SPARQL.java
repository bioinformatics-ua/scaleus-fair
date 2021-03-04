package pt.ua.scaleus.kbqa.querybuilding;

import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.ua.scaleus.kbqa.qald.QALD4_EvaluationUtils;

public class SPARQL {
	Logger log = LoggerFactory.getLogger(SPARQL.class);
	public QueryExecutionFactory qef;

	public SPARQL() {
		try {
			qef = new QueryExecutionFactoryHttp("http://dbpedia.org/sparql","http://dbpedia.org");
		} catch (RuntimeException e) {
			log.error("Could not create SPARQL interface! ", e);
			System.exit(0);
		}
	}

	public Set<RDFNode> sparql(final String query) {
		Set<RDFNode> set = Sets.newHashSet();
		try {
			QueryExecution qe = qef.createQueryExecution(query);
			if (qe != null && query.toString() != null) {
				if (QALD4_EvaluationUtils.isAskType(query)) {
					set.add(new ResourceImpl(String.valueOf(qe.execAsk())));
				} else {
					ResultSet results = qe.execSelect();
					while (results.hasNext()) {
						set.add(results.next().get("proj"));
					}
				}
			}
		} catch (Exception e) {
			log.error(query.toString(), e);
		}
		return set;
	}
}
