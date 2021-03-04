package pt.ua.scaleus.kbqa.pruner;

import java.util.Set;

import com.google.common.collect.Sets;

import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.querybuilding.SPARQLQuery;

public class ContainsProjVariable implements ISPARQLQueryPruner {

	public Set<SPARQLQuery> prune(Set<SPARQLQuery> queryStrings, KBQAQuestion q) {
		Set<SPARQLQuery> returnList = Sets.newHashSet();
		for (SPARQLQuery query : queryStrings) {
			String[] split = new String[3];

			for (String triple : query.constraintTriples) {
				split = triple.split(" ");
				boolean flag = false;
				if (split[0].equals("?proj")) {
					flag = true;
				}
				if (split[2].equals("?proj.")) {
					flag = true;
				}
				if (flag) {
					returnList.add(query);
				}

			}
		}
		return returnList;
	}
}
