package pt.ua.scaleus.kbqa.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;

public abstract class AbstractPipeline {
	abstract public List<Answer> getAnswersToQuestion(KBQAQuestion q);

	private static Logger log = LoggerFactory.getLogger(AbstractPipeline.class);

	/**
	 * 
	 * this doesnt get called anywhere. Keep this or delete and change
	 *           abstract class to interface?
	 *
	 */
	protected static void write(Set<EvalObj> evals) {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("results.html"));
			bw.write("<script src=\"sorttable.js\"></script><table class=\"sortable\">");
			bw.newLine();
			bw.write(" <tr>     <th>id</th><th>Question</th><th>F-measure</th><th>Precision</th><th>Recall</th><th>Comment</th>  </tr>");
			for (EvalObj eval : evals) {
				bw.write(" <tr>    <td>" + eval.getId() + "</td><td>" + eval.getQuestion() + "</td><td>" + eval.getFmax() + "</td><td>" + eval.getPmax() + "</td><td>" + eval.getRmax() + "</td><td>"
				        + eval.getComment() + "</td>  </tr>");
				bw.newLine();
			}
			bw.write("</table>");
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
