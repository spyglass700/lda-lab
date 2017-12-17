package org.tw.neinkeinkaffee.lda.model.converter;

import org.tw.neinkeinkaffee.lda.model.dto.DtoDocument;
import org.tw.neinkeinkaffee.lda.model.dto.DtoLda;
import org.tw.neinkeinkaffee.lda.model.dto.Topic;
import org.tw.neinkeinkaffee.lda.model.dto.probability.DocumentProbability;
import org.tw.neinkeinkaffee.lda.model.dto.probability.TopicProbability;
import org.tw.neinkeinkaffee.lda.model.dto.probability.WordProbability;
import org.tw.neinkeinkaffee.lda.model.dto.token.ContentToken;
import org.tw.neinkeinkaffee.lda.model.dto.token.StopToken;
import org.tw.neinkeinkaffee.lda.model.dto.word.ContentWord;
import org.tw.neinkeinkaffee.lda.model.dto.word.StopWord;
import org.tw.neinkeinkaffee.lda.model.lda.Lda;
import org.tw.neinkeinkaffee.lda.model.lda.PairCounter;
import org.tw.neinkeinkaffee.lda.model.lda.SimpleCounter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LdaToDtoLdaConverter implements Converter<Lda, DtoLda> {
    @Override
    public DtoLda convert(Lda lda) {
        PairCounter<Integer, String> topicWordCounts = lda.getTopicWordCounts();
        PairCounter<Integer, String> topicDocumentCounts = lda.getTopicDocumentCounts();
        PairCounter<String, Integer> wordTopicCounts = lda.getWordTopicCounts();
        PairCounter<String, Integer> documentTopicCounts = lda.getDocumentTopicCounts();

//        // TODO: This map has to be String (not Integer) -> Topic because MongoDB casts the Integer keys of the topicCounts into half-integer-half-strings that won't match to Integers but also can't be converted into proper Integers
//        HashMap<Integer, Topic> topicMap = new HashMap<Integer, Topic>(topicWordCounts.stream()
//            .map(entry -> Topic.builder()
//                .topicId(entry.getKey())
//                .build())
//            .collect(Collectors.toMap(topic -> topic.getTopicId(), topic -> topic)));
//
//        HashMap<String, ContentWord> wordMap = new HashMap<>(wordTopicCounts.stream()
//            .map(entry -> ContentWord.builder()
//                .lemma(entry.getKey())
//                .build())
//            .collect(Collectors.toMap(word -> word.getLemma(), word -> word)));
//
        // TODO: It would be nice to extract the nested document and token conversions, but how to achieve the linking of the references to the ContentWords from the wordMap into the ContentTokens in that case?
        HashMap<String, DtoDocument> documentMap = new HashMap<>(documentTopicCounts.stream()
            .map(entry -> DtoDocument.builder()
                .title(entry.getKey())
                .tokens(lda.getDocuments().get(entry.getKey()).getTokens().stream()
                    .map(token -> {
                        if (token.isStopword())
                            return StopToken.builder()
                                .word(StopWord.builder().lemma(token.getLemma()).build())
                                .build();
                        else
                            return ContentToken.builder()
                                .word(ContentWord.builder().lemma(token.getLemma()).build())
                                .topic(Topic.builder().topicId(token.getTopic()).build())
                                .build();
                    })
                    .collect(Collectors.toList()))
                .build())
            .collect((Collectors.toMap(document -> document.getTitle(), document -> document))));

        List<Topic> topics = topicWordCounts.stream()
            .map(counterEntry -> {
                int topicId = counterEntry.getKey();

                SimpleCounter<String> wordCounts = counterEntry.getValue();
                int sumOfWordCounts = wordCounts.stream()
                    .mapToInt(w -> w.getValue())
                    .sum();
                List<WordProbability> wordProbabilities = wordCounts.stream()
                    .map(wordCount -> WordProbability.builder()
                        .word(ContentWord.builder().lemma(wordCount.getKey()).build())
                        .probability((double) wordCount.getValue() / sumOfWordCounts)
                        .build())
                    .collect(Collectors.toList());
                Collections.sort(wordProbabilities, Collections.reverseOrder());

                SimpleCounter<String> documentCounts = topicDocumentCounts.getCount(topicId);
                int sumOfDocumentCounts = documentCounts.stream()
                    .mapToInt(d -> d.getValue())
                    .sum();
                List<DocumentProbability> documentProbabilities = documentCounts.stream()
                    .map(documentCount -> DocumentProbability.builder()
                        .document(DtoDocument.builder().title(documentCount.getKey()).build())
                        .probability((double) documentCount.getValue() / sumOfDocumentCounts)
                        .build())
                    .collect(Collectors.toList());
                Collections.sort(documentProbabilities, Collections.reverseOrder());

                return Topic.builder()
                    .topicId(topicId)
                    .wordProbabilities(wordProbabilities)
                    .documentProbabilities(documentProbabilities)
                    .build();
            })
            .collect(Collectors.toList());

        List<ContentWord> words = wordTopicCounts.stream()
            .map(counterEntry -> {
                String lemma = counterEntry.getKey();

                SimpleCounter<Integer> topicCounts = counterEntry.getValue();
                int sumOfTopicCounts = topicCounts.stream()
                    .mapToInt(topic -> topic.getValue())
                    .sum();
                List<TopicProbability> topicProbabilities = topicCounts.stream()
                    .map(topicCount -> TopicProbability.builder()
                        .topic(Topic.builder()
                            .topicId(topicCount.getKey())
                            .topWords(topics.get(topicCount.getKey()).getTopWordsSignature())
                            .build())
                        .probability((double) topicCount.getValue() / sumOfTopicCounts)
                        .build())
                    .collect(Collectors.toList());
                Collections.sort(topicProbabilities, Collections.reverseOrder());

                return ContentWord.builder()
                    .lemma(lemma)
                    .topicProbabilities(topicProbabilities)
                    .build();
            })
            .collect(Collectors.toList());

        List<DtoDocument> documents = documentTopicCounts.stream()
            .map(counterEntry -> {
                String title = counterEntry.getKey();

                SimpleCounter<Integer> topicCounts = counterEntry.getValue();
                int sumOfTopicCounts = topicCounts.stream()
                    .mapToInt(topic -> topic.getValue())
                    .sum();
                List<TopicProbability> topicProbabilities = topicCounts.stream()
                    .map(topicCount -> TopicProbability.builder()
                        .topic(Topic.builder()
                            .topicId(topicCount.getKey())
                            .topWords(topics.get(topicCount.getKey()).getTopWordsSignature())
                            .build())
                        .probability((double) topicCount.getValue() / sumOfTopicCounts)
                        .build())
                    .collect(Collectors.toList());
                Collections.sort(topicProbabilities, Collections.reverseOrder());

                DtoDocument document = documentMap.get(title);
                document.setTopicProbabilities(topicProbabilities);
                return document;
            })
            .collect(Collectors.toList());

        return DtoLda.builder()
            .corpusName(lda.getCorpusName())
            .numberOfTopics(lda.getNumberOfTopics())
            .topics(topics)
            .words(words)
            .documents(documents)
            .build();
    }
}
