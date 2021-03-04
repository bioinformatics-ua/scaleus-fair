package pt.ua.scaleus.kbqa.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ua.scaleus.kbqa.datastructures.Answer;
import pt.ua.scaleus.kbqa.datastructures.KBQAQuestion;
import pt.ua.scaleus.kbqa.nlp.MutableTreePruner;
import pt.ua.scaleus.kbqa.number.UnitController;
import pt.ua.scaleus.kbqa.querybuilding.Annotater;
import pt.ua.scaleus.kbqa.querybuilding.SPARQL;
import pt.ua.scaleus.kbqa.querybuilding.SPARQLQueryBuilder;
import pt.ua.scaleus.kbqa.spotter.ASpotter;
import pt.ua.scaleus.kbqa.spotter.Spotlight;

public class PipelineStanford extends AbstractPipeline {
	static Logger log = LoggerFactory.getLogger(PipelineStanford.class);
	private ASpotter nerdModule;
	private MutableTreePruner pruner;
	private Annotater annotater;
	private SPARQLQueryBuilder queryBuilder;
	private Cardinality cardinality;
	private QueryTypeClassifier queryTypeClassifier;
	private StanfordNLPConnector stanfordConnector;
	private UnitController numberToDigit;

	public PipelineStanford() {
		queryTypeClassifier = new QueryTypeClassifier();

		nerdModule = new Spotlight();
		// controller.nerdModule = new Spotlight();
		// controller.nerdModule =new TagMe();
		// controller.nerdModule = new MultiSpotter(fox, tagMe, wiki, spot);

		this.stanfordConnector = new StanfordNLPConnector();
		this.numberToDigit = new UnitController();
		numberToDigit.instantiateEnglish(stanfordConnector);
		cardinality = new Cardinality();

		pruner = new MutableTreePruner();

		SPARQL sparql = new SPARQL();
		annotater = new Annotater(sparql);

		queryBuilder = new SPARQLQueryBuilder(sparql);
	}

	@Override
	public List<Answer> getAnswersToQuestion(final KBQAQuestion q) {
		log.info("Question: " + q.getLanguageToQuestion().get("en"));

		log.info("Classify question type.");
		q.setIsClassifiedAsASKQuery(queryTypeClassifier.isASKQuery(q.getLanguageToQuestion().get("en")));

		// Disambiguate parts of the query
		log.info("Named entity recognition.");
		q.setLanguageToNamedEntites(nerdModule.getEntities(q.getLanguageToQuestion().get("en")));
		// Noun combiner, decrease #nodes in the DEPTree
		log.info("Noun phrase combination / Dependency Parsing");
		// TODO make tlhis method return the combine sequence and work on this,
		// i.e., q.sequence = sentenceToSequence.combineSequences(q);

		// This will calculate cardinality of reduced(combinedNN) tree?
		q.setTree(stanfordConnector.combineSequences(q, this.numberToDigit));
		// Cardinality identifies the integer i used for LIMIT i
		log.info("Cardinality calculation.");
		q.setCardinality(cardinality.cardinality(q));

		// Apply pruning rules
		log.info("Pruning tree.");
		q.setTree(pruner.prune(q));

		// Annotate tree
		log.info("Semantically annotating the tree.");
		annotater.annotateTree(q);

		// Calculating all possible SPARQL BGPs with given semantic annotations
		log.info("Calculating SPARQL representations.");
		List<Answer> answers = queryBuilder.build(q);

		return answers;
	}

	public static void main(final String[] args) {
		PipelineStanford p = new PipelineStanford();
		KBQAQuestion q = new KBQAQuestion();
		q.getLanguageToQuestion().put("en", "Which anti-apartheid activist was born in Mvezo?");
		p.getAnswersToQuestion(q);

	}

}
