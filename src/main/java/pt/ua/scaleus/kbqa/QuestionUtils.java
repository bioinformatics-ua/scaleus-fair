package pt.ua.scaleus.kbqa;

import java.util.Arrays;

public class QuestionUtils {

	public static String detectQW(String question) {
		String[] questionsWords = { "when", "who", "where", "what", "which", "how", "whom" };
		String[] tokens = question.split(" ");
		for (String token : tokens) {
			if (Arrays.asList(questionsWords).contains(token)) {
				return token;
			}
		}
		return "";
	}

	public static String detectAnsTypeSurface(String question) {
		String qw = detectQW(question);
		String[] words = { "which", "what" };
		for (String word : words) {
			if (qw.equals(word)) {
				String[] token = question.split(" ");
				int index = Arrays.asList(token).indexOf(word);
				if (index + 1 < token.length)
					return token[index + 1];
			}
		}
		return "";
	}
}
