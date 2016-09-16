package edu.memphis.iis;

import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import org.apache.commons.io.IOUtils;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class MatePlusProcessor {
//    private static String[] pipelineOptions = new String[]{
//            "eng",										// language
//            "-lemma", "models/lemma-eng.model",			// lemmatization model
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

        // Get the models we need from our model dependency
        File lemmaModel = extractMateModel("/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model");
        File parserModel = extractMateModel("/CoNLL2009-ST-English-ALL.anna-3.3.parser.model");
        File taggerModel = extractMateModel("/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model");

        // TODO: issue with the lemmatizer reader? see our TODO in Lemmatizer
        Lemmatizer lemmatizer = BohnetHelper.getLemmatizer(lemmaModel);
        Parser parser = BohnetHelper.getParser(parserModel);
        Tagger tagger = BohnetHelper.getTagger(taggerModel);


        Preprocessor pp = new PipelinedPreprocessor(tokenizer, lemmatizer, tagger, null, parser);

        //TODO: the options can't be null - we need to get rid of them or something
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

    // Extract the model specified from the mate-models package.
    //
    // We use the warning class from that package in case we are running in an
    // environment with multiple class loaders (like some web app servers).
    // Since we're using class.getResourceAsStream, all resource paths should
    // begin with a forward slash.
    //
    // See defaultRun for example usage
    protected File extractMateModel(String resourcePath) throws IOException {
        // Start reading resource
        InputStream readStream = edu.memphis.iis.warning.class.getResourceAsStream(resourcePath);
        if (readStream == null) {
            throw new IOException("Resource-based MATE Model cannot be found: " + resourcePath);
        }

        // TODO: we re-write the models every time. Use consistent file name in temp directory instead?
        // Setup the temp file
        File tempFile = File.createTempFile("mate", "model");
        tempFile.deleteOnExit();

        // Write to the temp file
        FileOutputStream writeStream = new FileOutputStream(tempFile);
        try {
            IOUtils.copy(readStream, writeStream);
        }
        finally {
            writeStream.close();
        }

        // All done
        return tempFile;
    }
}
