package pt.ua.scaleus.kbqa.pruner;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.querybuilding.SPARQLQuery;

public class CyclicTriple implements ISPARQLQueryPruner {
	public Set<SPARQLQuery> prune(Set<SPARQLQuery> queryStrings, KBQAQuestion q) {
		// discard queries containing
		// ?proj <http://dbpedia.org/ontology/deathPlace> ?const.
		// ?const <http://dbpedia.org/ontology/deathPlace> ?proj.
		Set<SPARQLQuery> returnSet = Sets.newHashSet();
		Map<String, String> triples = Maps.newHashMap();
		for (SPARQLQuery sparqlQuery : queryStrings) {
			String[] split = new String[3];
			boolean goodQuery = true;
			triples = Maps.newHashMap();
			for (String triple : sparqlQuery.constraintTriples) {
				split = triple.split(" ");
				if (split[0].startsWith("?") && split[2].startsWith("?")) {
					if (triples.containsKey(split[1])) {
						goodQuery = false;
					} else {
						triples.put(split[1], triple);
					}
				}
			}
			if (goodQuery) {
				returnSet.add(sparqlQuery);
			}
		}
		return returnSet;
	}
}
