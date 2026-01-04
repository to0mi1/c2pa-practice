package org.to0mi1.c2pa.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.to0mi1.c2pa.core.model.C2paManifest;

/**
 * C2PA Core APIとの通信を行うアダプタークラス。
 * <p>
 * Python実装のC2PA Core APIに対して、画像の署名と検証のリクエストを送信します。
 *
 * @see C2paManifest
 */
@Component
public class C2paApiAdapter {

    private final RestClient c2paRestClient;

    public C2paApiAdapter(@Qualifier("c2paRestClient") RestClient c2paRestClient) {
        this.c2paRestClient = c2paRestClient;
    }

    /**
     * 画像のC2PA署名を検証します。
     *
     * @param image    検証対象の画像データ
     * @param fileName ファイル名
     * @return C2PAマニフェスト（検証結果を含む）
     * @see C2paManifest
     */
    public C2paManifest verify(byte[] image, String fileName) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("image", new ByteArrayResource(image) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });

        return c2paRestClient.post()
                .uri("/verify")
                .body(parts)
                .retrieve()
                .body(C2paManifest.class);
    }

    /**
     * 画像にC2PA署名を付与します。
     *
     * @param title    画像のタイトル
     * @param image    署名対象の画像データ
     * @param fileName ファイル名
     * @param aiInference AI推論の制限
     * @param aiInferenceConstraintsInfo AI推論の制限詳細
     * @param aiGenerativeTraining AI生成学習の制限
     * @param aiGenerativeTrainingConstraintsInfo AI生成学習の制限詳細
     * @return 署名済み画像のバイト配列
     */
    public byte[] sign(String title, byte[] image, String fileName,
                       String aiInference, String aiInferenceConstraintsInfo,
                       String aiGenerativeTraining, String aiGenerativeTrainingConstraintsInfo) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("title", title);
        parts.add("image", new ByteArrayResource(image) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        if (aiInference != null) {
            parts.add("ai_inference", aiInference);
        }
        if (aiInferenceConstraintsInfo != null) {
            parts.add("ai_inference_constraints_info", aiInferenceConstraintsInfo);
        }
        if (aiGenerativeTraining != null) {
            parts.add("ai_generative_training", aiGenerativeTraining);
        }
        if (aiGenerativeTrainingConstraintsInfo != null) {
            parts.add("ai_generative_training_constraints_info", aiGenerativeTrainingConstraintsInfo);
        }

        return c2paRestClient.post()
                .uri("/sign")
                .body(parts)
                .retrieve()
                .body(byte[].class);
    }
}
