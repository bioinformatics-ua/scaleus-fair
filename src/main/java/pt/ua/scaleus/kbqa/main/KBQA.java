package pt.ua.scaleus.kbqa.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ua.scaleus.kbqa.controller.AbstractPipeline;
import pt.ua.scaleus.kbqa.controller.PipelineStanford;
import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.ranking.BucketRanker;
import pt.ua.scaleus.kbqa.ranking.TierRanker;
import org.apache.jena.query.*;
//import org.apache.jena.sparql.engine.http.QueryEngineHTT
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class KBQA {
	static Logger log = LoggerFactory.getLogger(KBQA.class);

	public static void main(String args[]) throws IOException, ParserConfigurationException {
//		AbstractPipeline pipeline = new PipelineStanford();
//		KBQAQuestion q = new KBQAQuestion();
//		q.getLanguageToQuestion().put("en", "Who is the president of the United States?");
//		List<Answer> answers = pipeline.getAnswersToQuestion(q);
//		BucketRanker bucket_ranker = new BucketRanker();
//		TierRanker tier = new TierRanker();
//		List<Answer> rankedAnswer = bucket_ranker.rank(answers, q);
//		rankedAnswer = tier.rank(answers, q);
//		System.out.println(Arrays.toString(rankedAnswer.toArray()));
		
        String queryStr = "SELECT ?prop ?place WHERE { <http://dbpedia.org/resource/%C3%84lvdalen> ?prop ?place .}";
        Query query = QueryFactory.create(queryStr);
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            ResultSet rs = qexec.execSelect();
            ResultSetFormatter.out(System.out, rs, query);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
