package pt.ua.scaleus.kbqa.ranking;

import java.util.List;

import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;

public interface Ranking {

	public List<Answer> rank(List<Answer> answers, KBQAQuestion q);

}
