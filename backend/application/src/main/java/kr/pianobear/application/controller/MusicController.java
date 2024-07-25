package kr.pianobear.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.pianobear.application.model.Music;
import kr.pianobear.application.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/music")
@Tag(name = "Music", description = "음악 악보 API")
public class MusicController {
    @Autowired
    private MusicService musicService;

    @GetMapping
    @Operation(summary = "악보 불러오기")
    public List<Music> getAllMusic(@RequestParam String userId) {
        return musicService.getAllMusic(userId);
    }

    @PostMapping
    @Operation(summary = "악보 추가")
    public void addMusic(@RequestBody Music music) {
        musicService.addMusic(music);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "악보 삭제")
    public void deleteMusic(@PathVariable int id) {
        musicService.deleteMusic(id);
    }

    @PutMapping("/favorite/{id}")
    @Operation(summary = "악보 찜")
    public void favoriteMusic(@PathVariable int id, @RequestParam boolean favorite) {
        musicService.favoriteMusic(id, favorite);
    }

    @GetMapping("/paged")
    public Page<Music> getPagedMusicList(@RequestParam String userId, Pageable pageable) { //front레서 어케 줌
        return musicService.getPagedMusicList(userId, pageable);
    }

    @GetMapping("/search/title")
    @Operation(summary = "악보 검색")
    public List<Music> searchMusicByTitle(@RequestParam String keyword) {
        return musicService.searchMusicByTitle(keyword);
    }

//    @GetMapping("/search/composer")
//    public List<Music> searchMusicByComposer(@RequestParam String keyword) {
//        return musicService.searchMusicByComposer(keyword);  // Updated to search by composer
//    }

    @GetMapping("/sorted")
    public List<Music> getSortedMusic(@RequestParam String userId, @RequestParam String sortType) {
        return musicService.getSortedMusic(userId, sortType);
    }
}
