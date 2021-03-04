package pt.ua.scaleus.kbqa.pruner;

import java.util.Set;

import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.querybuilding.SPARQLQuery;

public interface ISPARQLQueryPruner {

	public Set<SPARQLQuery> prune(Set<SPARQLQuery> queries, KBQAQuestion q);
}
