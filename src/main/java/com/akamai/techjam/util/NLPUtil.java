package com.akamai.techjam.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/**
 * Created by dsoni on 29/11/17.
 */
@Component
public class NLPUtil {

    private StanfordCoreNLPClient pipeline;

    public NLPUtil() throws Exception{
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,ner,sentiment");
        props.setProperty("language", "english");
        pipeline = new StanfordCoreNLPClient(props, "http://localhost", 9000, 2);
    }


    public static void main(String[] args) throws Exception {
// read some text in the text variable
        String[] text = {
                "#Android #hacking  \n" +
                "tizi spyware http://tizi.apch.test.dom.com spies on ur whatsapp and social media account's. " +
                "https://www.google.com, is a bad domain. Company found list of malicious domain, one of them is www.malwr.com. Test DGA IP 192.168.23.4."
        }; // Add your text here!

        final NLPUtil NLPUtil = new NLPUtil();
        //NLPUtil.init();
        System.out.println(Arrays.deepToString(NLPUtil.findDomainsOrIPs(NLPUtil.findSensitiveStrings(text))));
// create an empty Annotation just with the given text
        /*Annotation document = new Annotation(text);
// run all Annotators on this text
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.println(String.format(">>>> Output : Word : [%s], pos : [%s], ner: [%s]", word, pos, ne));
            }

        }*/
        //final Pattern patternIP = Pattern.compile("^.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]).$");
        /*Pattern pattern = Pattern.compile("^(([a-zA-Z]{1})|([a-zA-Z]{1}[a-zA-Z]{1})|([a-zA-Z]{1}[0-9]{1})|([0-9]{1}[a-zA-Z]{1})|([a-zA-Z0-9][a-zA-Z0-9-_]{1,61}[a-zA-Z0-9]))\\.([a-zA-Z]{2,6}|[a-zA-Z0-9-]{2,30}\\.[a-zA-Z]{2,3})$");
        Matcher matcher = null;
        matcher = pattern.matcher("google.com " +
                "maselkowski.pl " +
                "m.maselkowski.pl "
                );
        while(matcher.find()) {
            System.out.println("google.com\n" +
                    "masełkowski.pl\n" +
                    "maselkowski.pl\n" +
                    "m.maselkowski.pl\n" +
                    "www.masełkowski.pl.com\n" +
                    "xn--masekowski-d0b.pl".substring(matcher.start(), matcher.end()-1));
        }
        System.out.print(matcher.matches());*/
    }

    public String[] findSensitiveStrings(final String[] feeds) {
        List<String> result = new ArrayList<String>();
        for (String feed : feeds) {
            result.addAll(findSensitiveStrings(feed, 2));
        }
        return result.toArray(new String[result.size()]);
    }
    public List<String> findSensitiveStrings(final String feed, int sensitivity) {
        List<String> result = new ArrayList<String>();
        if(feed != null && feed.length() > 0) {
            final Annotation annotation = pipeline.process(feed);
            for(final CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class))
            {
                final Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                final int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                System.out.println(String.format(">>>> Output : Word : [%s], sentiment: [%s]", sentence.toString(), sentiment));
                if(sentiment < sensitivity) {
                    result.add(sentence.toString());
                }
            }
        }
        return result;
    }

    public String[] findDomainsOrIPs(String[] sensitiveData) {
        final List<String> domainsOrIPs = new ArrayList<String>();
        final Pattern patternDomain = Pattern.compile("^.*((([a-zA-Z])|([a-zA-Z][a-zA-Z])|([a-zA-Z][0-9])|([0-9][a-zA-Z]{1})|([a-zA-Z0-9][a-zA-Z0-9-_]{1,61}[a-zA-Z0-9]))\\.([a-zA-Z]{2,6}|[a-zA-Z0-9-]{2,30}\\.[a-zA-Z]{2,3})).*$");
        final Pattern patternIP = Pattern.compile("^.*(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])).*$");
        Matcher matcher = null;
        for(String data : sensitiveData) {
            for (String word : data.split("\\s")) {
                matcher = patternDomain.matcher(word);
                while(matcher.find()) {
                    domainsOrIPs.add(matcher.group());
                }
                matcher.reset();
                matcher = patternIP.matcher(word);
                while(matcher.find()) {
                    domainsOrIPs.add(matcher.group());
                }
                matcher.reset();
            }
        }
        return domainsOrIPs.toArray(new String[domainsOrIPs.size()]);
    }
}
