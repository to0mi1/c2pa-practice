package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * C2PAマニフェストのルートオブジェクト。
 * <p>
 * C2PA（Coalition for Content Provenance and Authenticity）は、デジタルコンテンツの出所と真正性を
 * 証明するための標準規格です。このクラスは、C2PAマニフェスト全体を表現し、コンテンツの来歴情報、
 * 検証結果、および関連するすべてのマニフェストを保持します。
 *
 * @see Manifest
 * @see ValidationResults
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class C2paManifest {

    /**
     * アクティブなマニフェストのURN（Uniform Resource Name）。
     * <p>
     * 複数のマニフェストが存在する場合、このフィールドは現在有効なマニフェストを識別します。
     * <p>
     * 形式: {@code "urn:c2pa:{UUID}"}
     */
    @JsonProperty("active_manifest")
    private String activeManifest;

    /**
     * URNをキーとするマニフェストのマップ。
     * <p>
     * コンテンツには複数のマニフェストが含まれる場合があり、それぞれが異なる編集履歴や
     * 証明情報を表します。キーはマニフェストのURN、値はマニフェストオブジェクトです。
     */
    @JsonProperty("manifests")
    private Map<String, Manifest> manifests;

    /**
     * マニフェストの検証結果。
     * <p>
     * アクティブなマニフェストに対する検証結果を含みます。検証には署名の確認、
     * ハッシュ値の照合、タイムスタンプの検証などが含まれます。
     */
    @JsonProperty("validation_results")
    private ValidationResults validationResults;

    /**
     * マニフェスト全体の検証状態。
     * <p>
     * 可能な値: {@code "Valid"}（有効）、{@code "Invalid"}（無効）など。
     * この値は検証結果の総合的な評価を示します。
     */
    @JsonProperty("validation_state")
    private String validationState;
}
