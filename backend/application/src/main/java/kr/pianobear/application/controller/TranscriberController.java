package kr.pianobear.application.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transcriber")
public class TranscriberController {

    @PostMapping(value = "/upload-audio")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public void uploadAudio() {

    }
}
