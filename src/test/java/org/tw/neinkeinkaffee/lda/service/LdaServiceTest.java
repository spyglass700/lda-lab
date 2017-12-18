package org.tw.neinkeinkaffee.lda.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.tw.neinkeinkaffee.lda.model.corpus.Corpus;
import org.tw.neinkeinkaffee.lda.model.dto.DtoDocument;
import org.tw.neinkeinkaffee.lda.model.dto.DtoLda;
import org.tw.neinkeinkaffee.lda.model.dto.Topic;
import org.tw.neinkeinkaffee.lda.model.lda.Lda;
import org.tw.neinkeinkaffee.lda.repository.ContentWordRepository;
import org.tw.neinkeinkaffee.lda.repository.DocumentRepository;
import org.tw.neinkeinkaffee.lda.repository.LdaRepository;
import org.tw.neinkeinkaffee.lda.repository.TopicRepository;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.hamcrest.core.Is.is;

public class LdaServiceTest {

    @Mock
    TopicRepository topicRepository;
    @Mock
    ContentWordRepository contentWordRepository;
    @Mock
    DocumentRepository documentRepository;

    @InjectMocks
    LdaService ldaService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFetchDtoLda() {
        // given
        DtoLda expectedDtoLda = DtoLda.builder()
            .corpusName("someCorpus")
            .numberOfTopics(3)
            .build();
        when(topicRepository.findAllByCorpusNameAndNumberOfTopics("someCorpus", 3))
            .thenReturn(new ArrayList<>());
        when(contentWordRepository.findAllByCorpusNameAndNumberOfTopics("someCorpus", 3))
            .thenReturn(new ArrayList<>());
        when(documentRepository.findAllByCorpusNameAndNumberOfTopics("someCorpus", 3))
            .thenReturn(new ArrayList<>());

        // when
        DtoLda dtoLda = ldaService.fetchBy("someCorpus", 3);

        // then
        assertThat(dtoLda.getCorpusName(), is("someCorpus"));
        assertThat(dtoLda.getNumberOfTopics(), is(3));
    }

}