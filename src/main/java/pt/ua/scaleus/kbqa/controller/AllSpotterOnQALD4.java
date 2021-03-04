package pt.ua.scaleus.kbqa.controller;

import java.util.List;

import org.aksw.qa.commons.datastructure.Entity;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.QALD_Loader;
import org.apache.jena.query.QueryParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestionFactory;
import pt.ua.scaleus.kbqa.spotter.ASpotter;
import pt.ua.scaleus.kbqa.spotter.Fox;
import pt.ua.scaleus.kbqa.spotter.Spotlight;
import pt.ua.scaleus.kbqa.spotter.TagMe;

public class AllSpotterOnQALD4 {
	static Logger log = LoggerFactory.getLogger(AllSpotterOnQALD4.class);

	public static void main(final String args[]) {

		List<KBQAQuestion> questions = KBQAQuestionFactory.createInstances(QALD_Loader.load(Dataset.QALD5_Train_Hybrid));
		for (KBQAQuestion q : questions) {
			log.info(q.getLanguageToQuestion().get("en"));
			try {
				for (ASpotter nerdModule : new ASpotter[] { new Spotlight(), new Fox(), new TagMe() }) {
					q.setLanguageToNamedEntites(nerdModule.getEntities(q.getLanguageToQuestion().get("en")));
					if (!q.getLanguageToNamedEntites().isEmpty()) {
						for (Entity ent : q.getLanguageToNamedEntites().get("en")) {
							log.info("\t" + nerdModule.toString() + "\t" + ent.toString());
						}
					}
				}
			} catch (QueryParseException e) {
				log.error("QueryParseException: " + q.getPseudoSparqlQuery(), e);
			}
		}
	}
}