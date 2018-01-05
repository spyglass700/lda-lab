package org.tw.neinkeinkaffee.lda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tw.neinkeinkaffee.lda.helper.FileToStringReader;
import org.tw.neinkeinkaffee.lda.model.CorpusName;
import org.tw.neinkeinkaffee.lda.model.corpus.Corpus;
import org.tw.neinkeinkaffee.lda.model.dto.LdaParameterCombination;
import org.tw.neinkeinkaffee.lda.service.CorpusService;
import org.tw.neinkeinkaffee.lda.service.LdaService;

import java.util.List;

@Controller
public class HomeController {
    private CorpusService corpusService;
    private LdaService ldaService;

    @Autowired
    public HomeController(final CorpusService corpusService, final LdaService ldaService) {
        this.corpusService = corpusService;
        this.ldaService = ldaService;
    }

    @RequestMapping("/")
    String home(Model model) {
        List<LdaParameterCombination> availableLdaModels = ldaService.fetchAll();
        model.addAttribute("availableModels", availableLdaModels);
        model.addAttribute("ldaParameterCombination", new LdaParameterCombination());
        List<CorpusName> availableCorpora = corpusService.fetchAll();
        model.addAttribute("availableCorpora", availableCorpora);
        return "home";
    }

    @RequestMapping("/createAllCorpora")
    String createAllCorpora() {
        String heCorpusString = FileToStringReader.readFileToString("/Users/gstupper/repos/lda-lab/src/test/resources/corpora/hcjswb_he_1827.txt");
        String raoCorpusString = FileToStringReader.readFileToString("/Users/gstupper/repos/lda-lab/src/test/resources/corpora/hcjswxb_rao_1881.txt");
        String shengCorpusString = FileToStringReader.readFileToString("/Users/gstupper/repos/lda-lab/src/test/resources/corpora/hcjswxb_sheng_1897.txt");
        String stopwordString = FileToStringReader.readFileToString("/Users/gstupper/repos/lda-lab/src/test/resources/corpora/hcjswb_stop.txt");
        corpusService.clearAll();
        Corpus heCorpus = Corpus.fromString("hcjswb_he_1827", heCorpusString, stopwordString);
        Corpus raoCorpus = Corpus.fromString("hcjswxb_rao_1881", raoCorpusString, stopwordString);
        Corpus shengCorpus = Corpus.fromString("hcjswxb_sheng_1897", shengCorpusString, stopwordString);
        corpusService.save(heCorpus);
        corpusService.save(raoCorpus);
        corpusService.save(shengCorpus);
        return "redirect:/";
    }

    // TODO: This should go into its own LdaController
    @RequestMapping("/corpus/{corpus_name}/numberOfTopics/{number_of_topics}/timestamp/{timestamp}/delete")
    String deleteLda(final @PathVariable("corpus_name") String corpusName,
                     final @PathVariable("number_of_topics") int numberOfTopics,
                     final @PathVariable("timestamp") String timestamp,
                     Model model) {
        ldaService.clearBy(corpusName, numberOfTopics, timestamp);
        return "redirect:/";
    }

    @RequestMapping("/deleteAll")
    String deleteAllLda(Model model) {
        ldaService.clearAll();
//        corpusService.clearAll();
        return "redirect:/";
    }

    @PostMapping("/createLda")
    public String createLda(@ModelAttribute LdaParameterCombination ldaParameterCombination) {
        ldaService.create(ldaParameterCombination);
        return "redirect:/";
    }
}