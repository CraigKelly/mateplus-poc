package edu.memphis.iis;

import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.Parse;
import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.preprocessor.PipelinedPreprocessor;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.tokenization.StanfordPTBTokenizer;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class MatePlusProcessor {
//    private static String[] pipelineOptions = new String[]{
//            "eng",										// language
//            "-lemma", "models/lemma-eng.model",			// lemmatization mdoel
//            "-tagger", "models/tagger-eng.model",		// tagger model
//            "-parser", "models/parse-eng.model",		// parsing model
//            "-srl", "models/srl-EMNLP14+fs-eng.model",	// SRL model
//            "-tokenize",								// turn on word tokenization
//            "-reranker"									// turn on reranking (part of SRL)
//    };

    /*
    * -tokenize set loadPreprocessorWithTokenizer = true, skipPI = false, desegment = false
    * */

    //TODO: accept inputStream or filename or something and then test on our sample file in test resources
    public void defaultRun() throws ClassNotFoundException, IOException {
        // TODO: how does this work on our test file?
        Tokenizer tokenizer = new StanfordPTBTokenizer();

        //TODO: fix the next 3 by importing the mate-tools (see Downloads) and change them to accept streams
        Lemmatizer lemmatizer = BohnetHelper.getLemmatizer(new File("lemmatizer model"));

        Tagger tagger = BohnetHelper.getTagger(new File("tagger model"));

        Parser parser = BohnetHelper.getParser(new File("parser model"));

        Preprocessor pp = new PipelinedPreprocessor(tokenizer, lemmatizer, tagger, null, parser);

        //TODO: with out the options
        Parse.parseOptions = null; //options.getParseOptions();
        SemanticRoleLabeler srl = new Reranker(Parse.parseOptions);

        CompletePipeline pipeline = new CompletePipeline(pp, srl);

        String text = "This is the first sentence of the rest of your life";

        String[] tokens = pipeline.pp.tokenize(text); // this is how you tokenize your text

        Sentence s = null;
        try {
            s = pipeline.parse(Arrays.asList(tokens));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }


        // a sentence is just a list of words
        int size = s.size();
        for(int i = 1; i<size; i++) {
            Word w = s.get(i); // skip word number 0 (ROOT token)
            // each word object contains information about a word's actual word form / lemma / POS
            System.out.println(w.getForm() + "\t " + w.getLemma() + "\t" + w.getPOS());
        }

        System.out.println();

        // some words in a sentence are recognized as predicates
        for(Predicate p : s.getPredicates()) {
            // every predicate has a sense that defines its semantic frame
            System.out.println(p.getForm() + " (" + p.getSense()+ ")");
            // show arguments from the semantic frame that are instantiated in a sentence
            for(Word arg : p.getArgMap().keySet()) {
                System.out.print("\t" + p.getArgMap().get(arg) + ":");
                // "arg" is just the syntactic head word; let's iterate through all words in the argument span
                for(Word w : arg.getSpan())
                    System.out.print(" " + w.getForm());
                System.out.println();
            }

            System.out.println();
        }

        //TODO: CoNLL 2009 SRL output
    }
}
