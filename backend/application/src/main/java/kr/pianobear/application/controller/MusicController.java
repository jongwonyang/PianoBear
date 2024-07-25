package kr.pianobear.application.controller;

import kr.pianobear.application.model.Music;
import kr.pianobear.application.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/music")
public class MusicController {
    @Autowired
    private MusicService musicService;

    @GetMapping("/all")
    public List<Music> getAllMusic(@RequestParam String userId) {
        return musicService.getAllMusic(userId);
    }

    @PostMapping("/add")
    public void addMusic(@RequestBody Music music) {
        musicService.addMusic(music);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMusic(@PathVariable int id) {
        musicService.deleteMusic(id);
    }

    @PutMapping("/favorite/{id}")
    public void favoriteMusic(@PathVariable int id, @RequestParam boolean favorite) {
        musicService.favoriteMusic(id, favorite);
    }

    @GetMapping("/paged")
    public Page<Music> getPagedMusicList(@RequestParam String userId, Pageable pageable) { //front레서 어케 줌
        return musicService.getPagedMusicList(userId, pageable);
    }

    @GetMapping("/search/title")
    public List<Music> searchMusicByTitle(@RequestParam String keyword) {
        return musicService.searchMusicByTitle(keyword);
    }

    @GetMapping("/search/composer")
    public List<Music> searchMusicByComposer(@RequestParam String keyword) {
        return musicService.searchMusicByComposer(keyword);  // Updated to search by composer
    }

    @GetMapping("/sorted")
    public List<Music> getSortedMusic(@RequestParam String userId, @RequestParam String sortType) {
        return musicService.getSortedMusic(userId, sortType);
    }
}
