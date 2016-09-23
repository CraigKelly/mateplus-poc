package edu.memphis.iis;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import is2.parser.Parser;
import is2.tag.Tagger;
import is2.util.DB;
import org.apache.commons.io.IOUtils;
import se.lth.cs.srl.CompletePipeline;
import se.lth.cs.srl.Parse;
import se.lth.cs.srl.SemanticRoleLabeler;
import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.io.CoNLL09Writer;
import se.lth.cs.srl.io.SentenceWriter;
import se.lth.cs.srl.languages.Language;
import se.lth.cs.srl.options.CompletePipelineCMDLineOptions;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.options.ParseOptions;
import se.lth.cs.srl.pipeline.Reranker;
import se.lth.cs.srl.preprocessor.PipelinedPreprocessor;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.tokenization.StanfordPTBTokenizer;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.preprocessor.tokenization.WhiteSpaceTokenizer;
import se.lth.cs.srl.util.BohnetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class MatePlusProcessor {
    //TODO: accept inputStream or filename or something and then test on our sample file in test resources
    //TODO: for unit test `throws Exception` is fine, but it MUST change when this is "real" code
    public void defaultRun() throws Exception {
        // For now we are constrained to English
        Language.setLanguage(Language.L.eng);

        Tokenizer tokenizer =  new StanfordPTBTokenizer();

        // Get the models we need from our model dependency
        File lemmaModel = extractMateModel("/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model");
        File parserModel = extractMateModel("/CoNLL2009-ST-English-ALL.anna-3.3.parser.model");
        File taggerModel = extractMateModel("/CoNLL2009-ST-English-ALL.anna-3.3.postagger.model");
        File srlModel = extractMateModel("/srl-EMNLP14+fs-eng.model");

        CompletePipelineCMDLineOptions parseOptions = new CompletePipelineCMDLineOptions();
        parseOptions.lemmatizer = lemmaModel;
        parseOptions.parser = parserModel;
        parseOptions.tagger = taggerModel;
        parseOptions.reranker = true;
        parseOptions.srl = srlModel;
        parseOptions.loadPreprocessorWithTokenizer = true;
        parseOptions.skipPI = false;
        parseOptions.desegment = false;
        Parse.parseOptions = parseOptions.getParseOptions();

        Lemmatizer lemmatizer = BohnetHelper.getLemmatizer(lemmaModel);
        Tagger tagger = BohnetHelper.getTagger(taggerModel);
        Parser parser = BohnetHelper.getParser(parserModel);
        Preprocessor pp = new PipelinedPreprocessor(tokenizer, lemmatizer, tagger, null, parser);
        SemanticRoleLabeler srl = new Reranker(Parse.parseOptions);
        CompletePipeline pipeline = new CompletePipeline(pp, srl);

        SentenceWriter writer = new CoNLL09Writer();  // stdout writer

        synchronized (parser) {
            DB.println("Synching on parser as safety measure");
        }

        String text = "No one actually knows what drives reef resilience or even what a coral reef looks like as it's rebounding.";

        String[] tokens = tokenizer.tokenize(text);
        SentenceData09 instance = new SentenceData09();
        instance.init(tokens);

        instance = lemmatizer.apply(instance);
        instance = tagger.apply(instance);

        // No mtagger
        instance.pfeats = new String[instance.forms.length];
        Arrays.fill(instance.pfeats, "_");

        synchronized (parser) {
            instance = parser.apply(instance);
        }

        Sentence processed = new Sentence(instance, false);
        srl.parseSentence(processed);

        System.out.println("");
        System.out.println("Actual Output:");
        writer.write(processed);

        System.out.println("");
        System.out.println("Some additional output for good measure");

        // a sentence is just a list of words
        int size = processed.size();
        for(int i = 1; i<size; i++) {
            Word w = processed.get(i); // skip word number 0 (ROOT token)
            // each word object contains information about a word's actual word form / lemma / POS
            System.out.println(w.getForm() + "\t " + w.getLemma() + "\t" + w.getPOS());
        }

        System.out.println();

        // some words in a sentence are recognized as predicates
        for(Predicate p : processed.getPredicates()) {
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
        // Figure out temp file name - if it's already there just return it
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = new File(baseDir, "mate-" + resourcePath.replaceAll("/", "_") + ".model");
        if (tempFile.exists() && tempFile.length() > 0) {
            DB.println("Using pre-existing model file: " + tempFile.getCanonicalPath());
            return tempFile;
        }

        // Start reading resource
        InputStream readStream = edu.memphis.iis.warning.class.getResourceAsStream(resourcePath);
        if (readStream == null) {
            throw new IOException("Resource-based MATE Model cannot be found: " + resourcePath);
        }

        // Write to the temp file
        DB.println("Writing model file to: " + tempFile.getCanonicalPath());
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
