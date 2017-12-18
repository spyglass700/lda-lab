package org.tw.neinkeinkaffee.lda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tw.neinkeinkaffee.lda.model.dto.word.ContentWord;
import org.tw.neinkeinkaffee.lda.repository.ContentWordRepository;

import java.util.List;

@Service
public class WordService {
    private ContentWordRepository contentWordRepository;

    @Autowired
    public WordService(final ContentWordRepository contentWordRepository) {
        this.contentWordRepository = contentWordRepository;
    }

    public List<ContentWord> fetchAllBy(String corpusName, int numberOfTopics) {
        return contentWordRepository.findAllByCorpusNameAndNumberOfTopics(corpusName, numberOfTopics);
    }

    public ContentWord fetchBy(String corpusName, int numberOfTopics, String lemma) {
        return contentWordRepository.findByCorpusNameAndNumberOfTopicsAndLemma(corpusName, numberOfTopics, lemma);
    }
}