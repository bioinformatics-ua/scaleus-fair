package pt.ua.scaleus.kbqa.datastructures;

import java.util.ArrayList;
import java.util.List;

import org.aksw.qa.commons.datastructure.IQuestion;

import pt.ua.scaleus.kbqa.qald.QALD4_EvaluationUtils;

public class KBQAQuestionFactory {

	public static KBQAQuestion createInstance(IQuestion q) {
		KBQAQuestion hq = new KBQAQuestion();

		hq.setId(q.getId());
		hq.setAnswerType(q.getAnswerType());
		hq.setPseudoSparqlQuery(q.getPseudoSparqlQuery());
		hq.setSparqlQuery(q.getSparqlQuery());
		hq.setAggregation(Boolean.TRUE.equals(q.getAggregation()));
		hq.setOnlydbo(Boolean.TRUE.equals(q.getOnlydbo()));
		hq.setOutOfScope(Boolean.TRUE.equals(q.getOutOfScope()));
		hq.setHybrid(Boolean.TRUE.equals(q.getHybrid()));

		boolean b = QALD4_EvaluationUtils.isAskType(q.getSparqlQuery());
		b |= QALD4_EvaluationUtils.isAskType(q.getPseudoSparqlQuery());
		hq.setLoadedAsASKQuery(b);

		hq.setLanguageToQuestion(q.getLanguageToQuestion());
		hq.setLanguageToKeywords(q.getLanguageToKeywords());
		hq.setGoldenAnswers(q.getGoldenAnswers());
		return hq;
	}

	public static List<KBQAQuestion> createInstances(List<IQuestion> qList) {
		ArrayList<KBQAQuestion> hq = new ArrayList<KBQAQuestion>();
		for (IQuestion q : qList) {
			hq.add(KBQAQuestionFactory.createInstance(q));
		}
		return hq;
	}

}
