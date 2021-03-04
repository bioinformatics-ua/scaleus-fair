package pt.ua.scaleus.kbqa.pruner;

import java.util.Set;

import com.google.common.collect.Sets;

import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.querybuilding.SPARQLQuery;

public class ContainsTooManyNodesAsTextLookUp implements ISPARQLQueryPruner {

	public Set<SPARQLQuery> prune(Set<SPARQLQuery> queryStrings, KBQAQuestion q) {
		Set<SPARQLQuery> returnList = Sets.newHashSet();
		for (SPARQLQuery query : queryStrings) {
			// assume only one variable left
			for (String variable : query.textMapFromVariableToCombinedNNExactMatchToken.keySet()) {
				if (query.textMapFromVariableToCombinedNNExactMatchToken.get(variable).size() <= 2) {
					returnList.add(query);
				}
			}
		}
		return returnList;
	}
}
