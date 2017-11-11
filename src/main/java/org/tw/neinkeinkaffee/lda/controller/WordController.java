package org.tw.neinkeinkaffee.lda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tw.neinkeinkaffee.lda.model.Lda;
import org.tw.neinkeinkaffee.lda.service.LdaService;

@Controller
public class WordController {
    private LdaService ldaService;

    @Autowired
    public WordController(final LdaService ldaService) {
        this.ldaService = ldaService;
    }

    @RequestMapping("/corpus/{corpus_id}/numberOfTopics/{number_of_topics}/word/{word_lemma}")
    String listTopic(final @PathVariable("corpus_id") int corpusId,
                     final @PathVariable("number_of_topics") int numberOfTopics,
                     final @PathVariable("word_lemma") String wordLemma,
                     Model model) {
        Lda lda = ldaService.getByCorpusNameAndNumberOfTopics(corpusId, numberOfTopics);
        model.addAttribute("word", lda.getWords().get(wordLemma));
        return "word";
    }
}