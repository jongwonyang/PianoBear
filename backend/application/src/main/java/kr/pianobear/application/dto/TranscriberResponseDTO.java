package kr.pianobear.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TranscriberResponseDTO {

    @JsonProperty("all_midi")
    private String allMidi;

    @JsonProperty("piano_midi")
    private String pianoMidi;

    @JsonProperty("other_inst_midi")
    private String otherInstMidi;

    @JsonProperty("piano_mxl")
    private String pianoMxl;
}
