package pt.ua.scaleus.kbqa.experiment;

import java.util.List;
import java.util.Map;

import org.aksw.qa.commons.datastructure.Entity;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.QALD_Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import pt.ua.scaleus.kbqa.controller.StanfordNLPConnector;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestionFactory;
import pt.ua.scaleus.kbqa.nlp.SentenceToSequence;
import pt.ua.scaleus.kbqa.spotter.Spotlight;

public class NounPhraseIdentification {
	static Logger log = LoggerFactory.getLogger(NounPhraseIdentification.class);

	public static void main(String[] args) {
		// initialize both noun phrase combiners

		// load questions

		// for each question

		// -- calculate noun phrases with both combiners
		// -- extract noun phrases from questions via SPARQL text:query
		// attribute
		// -- measure p,r,f

		// /---------------
		Spotlight nerdModule = new Spotlight();
		List<IQuestion> loadedQuestions = QALD_Loader.load(Dataset.QALD6_Train_Hybrid);
		List<KBQAQuestion> questionsStanford = KBQAQuestionFactory.createInstances(loadedQuestions);
		
		StanfordNLPConnector connector = new StanfordNLPConnector();
		for (KBQAQuestion currentQuestion : questionsStanford) {
			log.info(currentQuestion.getLanguageToQuestion().get("en"));
			currentQuestion.setLanguageToNamedEntites(nerdModule.getEntities(currentQuestion.getLanguageToQuestion().get("en")));
			// Annotation doc = stanford.runAnnotation(currentQuestion);
			connector.combineSequences(currentQuestion);
			// stanford.combineSequences(doc, currentQuestion);
			Map<String, List<Entity>> languageToNounPhrases = currentQuestion.getLanguageToNounPhrases();
			if (languageToNounPhrases != null && !languageToNounPhrases.isEmpty()) {
				log.info("Stanford:" +Joiner.on(", ").skipNulls().join(languageToNounPhrases.get("en")));
			}
		}
		List<KBQAQuestion> questionsClear = KBQAQuestionFactory.createInstances(loadedQuestions);

		
		for (KBQAQuestion currentQuestion : questionsClear) {
			log.info(currentQuestion.getLanguageToQuestion().get("en"));
			currentQuestion.setLanguageToNamedEntites(nerdModule.getEntities(currentQuestion.getLanguageToQuestion().get("en")));
			SentenceToSequence.combineSequences(currentQuestion);

			Map<String, List<Entity>> languageToNounPhrases = currentQuestion.getLanguageToNounPhrases();
			if (languageToNounPhrases != null && !languageToNounPhrases.isEmpty()) {
				log.info("Clear:" +Joiner.on(", ").skipNulls().join(languageToNounPhrases.get("en")));
			}
		}

		// try {
		//
		// File file = new File("stanford_compound.txt");
		//
		// file.createNewFile();
		//
		// FileWriter fw = new FileWriter(file.getAbsoluteFile());
		// BufferedWriter bw = new BufferedWriter(fw);
		// // !!!!bw.write(out.toString());
		// bw.close();
		//
		// log.debug("Done");
		//
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
	}
}
