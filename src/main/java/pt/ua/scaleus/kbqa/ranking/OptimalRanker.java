package pt.ua.scaleus.kbqa.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.RDFNode;

import com.google.common.collect.Maps;

import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.qald.QALD4_EvaluationUtils;

/**
 * optimal in a sense of fmeasure
 * 
 *
 */
public class OptimalRanker implements Ranking {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Answer> rank(final List<Answer> answers, final KBQAQuestion q) {
		// Compare to set of resources from benchmark
		// save score for for answerSet with fmeasure > 0
		Map<Answer, Double> buckets = Maps.newHashMap();

		for (Answer answer : answers) {
			Set<RDFNode> answerSet = answer.answerSet;
			// TODO check whether q has a gold standard set
			// should be checked in qa-commons
			double fMeasure = QALD4_EvaluationUtils.fMeasure(answerSet, q);

			if (fMeasure > 0) {
				buckets.put(answer, fMeasure);
			}
		}

		// sort according to entries in buckets
		List tmplist = new LinkedList(buckets.entrySet());

		Collections.sort(tmplist, new Comparator() {
			@Override
			public int compare(final Object o1, final Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		List list = new ArrayList<Answer>();
		for (Iterator it = tmplist.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			list.add(entry.getKey());
		}

		return list;
	}

}
