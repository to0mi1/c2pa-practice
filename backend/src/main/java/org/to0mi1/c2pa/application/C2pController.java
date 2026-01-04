package org.to0mi1.c2pa.application;

import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.to0mi1.c2pa.application.dto.VerifyResponse;
import org.to0mi1.c2pa.application.mapper.VerifyResponseMapper;
import org.to0mi1.c2pa.core.C2paApiAdapter;
import org.to0mi1.c2pa.core.model.C2paManifest;

import java.io.IOException;

@RestController
@RequestMapping(path = "/api/c2pa")
@Validated
public class C2pController {

    private final C2paApiAdapter c2paApiAdapter;
    private final VerifyResponseMapper verifyResponseMapper;

    public C2pController(C2paApiAdapter c2paApiAdapter, VerifyResponseMapper verifyResponseMapper) {
        this.c2paApiAdapter = c2paApiAdapter;
        this.verifyResponseMapper = verifyResponseMapper;
    }

    /**
     * 画像に署名を付与します。
     *
     * @param title 画像のタイトル
     * @param image 署名対象の画像ファイル
     * @return 署名済み画像のバイト配列
     * @throws IOException ファイル読み込みエラー
     */
    @PostMapping(value = "/sign")
    public byte[] sign(
            @RequestParam("title") String title,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "ai_inference", required = false) @Pattern(regexp = "allowed|notAllowed|constrained") String aiInference,
            @RequestParam(value = "ai_inference_constraints_info", required = false) String aiInferenceConstraintsInfo,
            @RequestParam(value = "ai_generative_training", required = false) @Pattern(regexp = "allowed|notAllowed|constrained") String aiGenerativeTraining,
            @RequestParam(value = "ai_generative_training_constraints_info", required = false) String aiGenerativeTrainingConstraintsInfo) throws IOException {
        return c2paApiAdapter.sign(
                title,
                image.getBytes(),
                image.getOriginalFilename(),
                aiInference,
                aiInferenceConstraintsInfo,
                aiGenerativeTraining,
                aiGenerativeTrainingConstraintsInfo
        );
    }

    /**
     * 画像のC2PA署名を検証します。
     *
     * @param image 検証対象の画像ファイル
     * @return 検証結果（主要な項目を抜粋）
     * @throws IOException ファイル読み込みエラー
     * @see VerifyResponse
     */
    @PostMapping("/verify")
    public VerifyResponse verify(@RequestParam("image") MultipartFile image) throws IOException {
        C2paManifest c2paManifest = c2paApiAdapter.verify(image.getBytes(), image.getOriginalFilename());
        return verifyResponseMapper.toVerifyResponse(c2paManifest);
    }
}
