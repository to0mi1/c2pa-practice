package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 検証結果。
 * <p>
 * C2PAマニフェストの検証結果を保持します。
 * 検証には、署名の確認、ハッシュ値の照合、タイムスタンプの検証、
 * アサーションの整合性チェックなどが含まれます。
 *
 * @see ActiveManifestValidation
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationResults {

    /**
     * アクティブなマニフェストの検証結果。
     * <p>
     * 現在アクティブなマニフェストに対する詳細な検証結果を含みます。
     * 成功、失敗、情報提供の3つのカテゴリに分類された検証ステータスのリストが含まれます。
     */
    @JsonProperty("activeManifest")
    private ActiveManifestValidation activeManifest;
}
