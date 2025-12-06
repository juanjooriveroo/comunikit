package boardservice.controller;

import boardservice.dto.*;
import boardservice.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Tag(
        name = "Tablero",
        description = "Endpoints para la gestión y uso de tableros y pictográmas"
)
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/pictogram")
    public ResponseEntity<?> pushPictogram(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody PictogramPushRequestDto request
    ) {
        PictogramDto response = boardService.pushPictogram(userId, request);
        return ResponseEntity.created(URI.create("/board/pictogram/" + response.id())).body(response);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> pushImage(
            @RequestHeader("X-User-ID") String userId,
            @RequestPart("data") ImagePushRequestDto request,
            @RequestPart("file") MultipartFile file
    ) {
        ImageDto response = boardService.pushImage(userId, request, file);
        return ResponseEntity.created(URI.create("/board/image/" + response.id())).body(response);
    }


    @GetMapping("/getAll-pictograms")
    public ResponseEntity<?> getAll(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody GetAllPictogramsRequestDto request
            ) {
        List<PictogramDto> response = boardService.getAllPictograms(userId, request);
        return ResponseEntity.ok(response);
    }
}