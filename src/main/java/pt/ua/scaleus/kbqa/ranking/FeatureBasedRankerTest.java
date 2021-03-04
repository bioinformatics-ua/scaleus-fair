package pt.ua.scaleus.kbqa.ranking;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.querybuilding.SPARQLQuery;
import pt.ua.scaleus.kbqa.ranking.FeatureBasedRanker.Feature;

public class FeatureBasedRankerTest {

	@Test
	// TODO transform this to unit test (not working yet, ranking not
	// implemented)
	@Ignore
	public void test() {

		List<SPARQLQuery> queries = Lists.newArrayList();
		// Which actress starring in the TV series Friends owns the production
		// company Coquette Productions?
		SPARQLQuery query = new SPARQLQuery("?const <http://dbpedia.org/ontology/starring> ?proj.");
		query.addFilterOverAbstractsContraint("?proj", "Friends");
		queries.add(query);

		query = new SPARQLQuery("?const ?verb ?proj.");
		query.addFilterOverAbstractsContraint("?proj", "Coquette Productions");
		query.addConstraint("?proj <http://dbpedia.org/ontology/owner> ?const");
		queries.add(query);
		System.out.println("queries:");
		System.out.println(queries);
		FeatureBasedRanker ranker = new FeatureBasedRanker();
		Logger logger = LoggerFactory.getLogger(FeatureBasedRanker.class);
		for (Set<Feature> featureSet : Sets.powerSet(new HashSet<>(Arrays.asList(Feature.values())))) {
			if (!featureSet.isEmpty()) {
				logger.debug("Feature-based ranking: " + featureSet.toString());
				ranker.setFeatures(featureSet);

				ranker.train();
				KBQAQuestion quest = new KBQAQuestion();
				// System.out.println(queries);

				List<Answer> answers = Lists.newArrayList();
				for (SPARQLQuery q : queries) {
					answers.add(q.toAnswer());
				}
				System.out.println("answers:");
				System.out.println(answers);

				List<Answer> rankedanswers = ranker.rank(answers, quest);
				System.out.println(rankedanswers);
				List<SPARQLQuery> returnqueries = Lists.newArrayList();
				for (Answer ans : rankedanswers) {
					returnqueries.add(ans.toSPARQLQuery());
				}
				for (SPARQLQuery q : returnqueries) {
					logger.debug(q.toString());
				}
				System.out.println("rankedanswers:");
				System.out.println(rankedanswers);
				System.out.println("returnqueries:");
				System.out.println(returnqueries);
			}
		}
	}

}
