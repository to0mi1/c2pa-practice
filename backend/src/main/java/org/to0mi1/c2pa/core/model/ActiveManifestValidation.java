package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * アクティブなマニフェストの検証結果。
 * <p>
 * アクティブなマニフェストに対する検証結果を、成功、失敗、情報提供の
 * 3つのカテゴリに分類して保持します。各カテゴリには、個別の検証項目の
 * ステータスがリストとして含まれます。
 *
 * @see ValidationStatus
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActiveManifestValidation {

    /**
     * 失敗した検証項目のリスト。
     * <p>
     * マニフェストの検証で失敗した項目を含みます。
     * 失敗項目が存在する場合、マニフェストは無効と見なされる可能性があります。
     * <p>
     * 例: 署名の検証失敗、ハッシュ値の不一致など。
     */
    @JsonProperty("failure")
    private List<ValidationStatus> failure;

    /**
     * 情報提供の検証項目のリスト。
     * <p>
     * 検証の過程で得られた情報や警告を含みます。
     * これらは必ずしもエラーではありませんが、注意が必要な場合があります。
     * <p>
     * 例: 信頼されていないタイムスタンプ証明書、推奨されない設定など。
     */
    @JsonProperty("informational")
    private List<ValidationStatus> informational;

    /**
     * 成功した検証項目のリスト。
     * <p>
     * マニフェストの検証で成功した項目を含みます。
     * <p>
     * 例: 署名の検証成功、ハッシュ値の一致、タイムスタンプの検証成功など。
     */
    @JsonProperty("success")
    private List<ValidationStatus> success;
}
