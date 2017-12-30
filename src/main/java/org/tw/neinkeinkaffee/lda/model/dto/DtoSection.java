package org.tw.neinkeinkaffee.lda.model.dto;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DtoSection {
    @Id
    private String id;
    @Getter
    private String title;
    @Getter
    @Setter
    String corpusName;
    @Getter
    @Setter
    int numberOfTopics;
    @Getter @Setter
    String timestamp;
    @Getter
    private String author;
    @Singular
    @Getter
    private List<DtoVolume> volumes;
}
