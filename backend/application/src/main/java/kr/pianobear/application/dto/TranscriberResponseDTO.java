package kr.pianobear.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TranscriberResponseDTO {

    @JsonProperty("original_midi")
    private String originalMidi;

    @JsonProperty("piano_midi")
    private String pianoMidi;

    @JsonProperty("other_instruments_midi")
    private String otherInstMidi;

    @JsonProperty("piano_musicxml")
    private String pianoMusicxml;
}
