package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * アクション（操作）。
 * <p>
 * コンテンツに対して実行された特定の操作や変更を表します。
 * C2PAでは、コンテンツの来歴を追跡するために、作成、編集、変換などの
 * すべての重要な操作をアクションとして記録します。
 *
 * @see AssertionData
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {

    /**
     * アクションの種類。
     * <p>
     * 実行された操作の種類を示すC2PA標準のアクション識別子。
     * <p>
     * 例:
     * <ul>
     *   <li>{@code "c2pa.created"} - コンテンツの作成</li>
     *   <li>{@code "c2pa.edited"} - コンテンツの編集</li>
     *   <li>{@code "c2pa.filtered"} - フィルタの適用</li>
     *   <li>{@code "c2pa.cropped"} - トリミング</li>
     * </ul>
     */
    @JsonProperty("action")
    private String action;

    /**
     * デジタルソースタイプ。
     * <p>
     * コンテンツのデジタルソースの種類を示すIPTC標準のURI。
     * <p>
     * 例:
     * <ul>
     *   <li>{@code "http://cv.iptc.org/newscodes/digitalsourcetype/digitalCreation"} - デジタル作成</li>
     *   <li>{@code "http://cv.iptc.org/newscodes/digitalsourcetype/digitalCapture"} - デジタルキャプチャ</li>
     *   <li>{@code "http://cv.iptc.org/newscodes/digitalsourcetype/trainedAlgorithmicMedia"} - AI生成</li>
     * </ul>
     * <p>
     * このフィールドは、特に作成アクション（{@code "c2pa.created"}）で使用されます。
     */
    @JsonProperty("digitalSourceType")
    private String digitalSourceType;
}
