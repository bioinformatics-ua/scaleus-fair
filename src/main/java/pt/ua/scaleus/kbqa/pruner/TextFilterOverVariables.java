package pt.ua.scaleus.kbqa.pruner;

import java.util.Set;

import com.google.common.collect.Sets;

import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.querybuilding.SPARQLQuery;

public class TextFilterOverVariables implements ISPARQLQueryPruner {
	private int maximalVariables = 1;

	public Set<SPARQLQuery> prune(Set<SPARQLQuery> queryStrings, KBQAQuestion q) {
		Set<SPARQLQuery> returnList = Sets.newHashSet();
		for (SPARQLQuery query : queryStrings) {
			if (query.textMapFromVariableToSingleFuzzyToken.size() <= maximalVariables) {
				returnList.add(query);
			}
		}
		return returnList;
	}
}
