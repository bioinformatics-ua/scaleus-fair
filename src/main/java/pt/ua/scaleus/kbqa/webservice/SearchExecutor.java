package pt.ua.scaleus.kbqa.webservice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import pt.ua.scaleus.kbqa.controller.AbstractPipeline;
import pt.ua.scaleus.kbqa.controller.PipelineStanford;
import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.ranking.BucketRanker;

@Service("searchExecutor")
public class SearchExecutor {
	private AbstractPipeline pipeline = new PipelineStanford();
	private Logger log = LoggerFactory.getLogger(SearchExecutor.class);

	// public Future<KBQAQuestion> search(KBQAQuestion q) {
	// log.info("Run pipeline on " + q.getLanguageToQuestion().get("en"));
	// List<Answer> answers = pipeline.getAnswersToQuestion(q);
	//
	// // FIXME improve ranking, put other ranking method here
	// // bucket-based ranking
	// BucketRanker bucket_ranker = new BucketRanker();
	// log.info("Bucket-based ranking");
	// List<Answer> rankedAnswer = bucket_ranker.rank(answers, q);
	// log.info(Joiner.on("\n\t").join(rankedAnswer));
	// q.setFinalAnswer(rankedAnswer);
	//
	// return new AsyncResult<KBQAQuestion>(q);
	// }

	public String runPipeline(String question) {
		KBQAQuestion q = new KBQAQuestion();
		q.getLanguageToQuestion().put("en", question);
		log.info("Run pipeline on " + q.getLanguageToQuestion().get("en"));
		List<Answer> answers = pipeline.getAnswersToQuestion(q);

		BucketRanker bucket_ranker = new BucketRanker();
		log.info("Bucket-based ranking");
		List<Answer> rankedAnswer = bucket_ranker.rank(answers, q);
		log.info(Joiner.on("\n\t").join(rankedAnswer));
		q.setFinalAnswer(rankedAnswer);
		return q.getJSONStatus();
	}
}
