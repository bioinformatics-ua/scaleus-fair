package pt.ua.scaleus.kbqa.ranking;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.ua.scaleus.kbqa.controller.EvalObj;
import pt.ua.scaleus.kbqa.controller.PipelineClearNLP;
import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.experiment.Measures;
import pt.ua.scaleus.kbqa.experiment.SingleQuestionPipeline;

public class OptimalRankerTest {
	// TODO Optimal ranking not implemented correctly yet
	static Logger log = LoggerFactory.getLogger(SingleQuestionPipeline.class);

	@Test
	@Ignore
	public void test() {

		PipelineClearNLP pipeline = new PipelineClearNLP();

		KBQAQuestion q = new KBQAQuestion();
		q.getLanguageToQuestion().put("en", "Which actress starring in the TV series Friends owns the production company Coquette Productions?");

		log.info("Run pipeline on " + q.getLanguageToQuestion().get("en"));
		List<Answer> answers = pipeline.getAnswersToQuestion(q);

		// ##############~~RANKING~~##############
		log.info("Run ranking");
		int maximumPositionToMeasure = 10;
		OptimalRanker optimal_ranker = new OptimalRanker();

		log.info("Optimal ranking not applicable (right now).");
		List<Answer> rankedAnswer = optimal_ranker.rank(answers, q);
		List<EvalObj> eval = Measures.measure(rankedAnswer, q, maximumPositionToMeasure);
		log.info(Joiner.on("\n\t").join(eval));

	}
}
