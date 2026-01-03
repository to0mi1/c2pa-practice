package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 検証ステータス。
 * <p>
 * マニフェストの検証における個別の検証項目の結果を表します。
 * 各検証項目には、コード、説明、および関連するURLが含まれます。
 * これにより、検証の詳細な結果を追跡し、問題の診断を容易にします。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationStatus {

    /**
     * 検証ステータスコード。
     * <p>
     * 検証項目を識別するコード。C2PA標準で定義されたコードが使用されます。
     * <p>
     * 例:
     * <ul>
     *   <li>{@code "claimSignature.validated"} - クレーム署名の検証成功</li>
     *   <li>{@code "timeStamp.validated"} - タイムスタンプの検証成功</li>
     *   <li>{@code "timeStamp.untrusted"} - タイムスタンプ証明書が信頼されていない</li>
     *   <li>{@code "assertion.hashedURI.match"} - アサーションのハッシュURIが一致</li>
     *   <li>{@code "assertion.dataHash.match"} - データハッシュが一致</li>
     * </ul>
     */
    @JsonProperty("code")
    private String code;

    /**
     * 検証結果の説明。
     * <p>
     * 検証項目の結果を人間が読める形式で説明します。
     * <p>
     * 例: {@code "claim signature valid"}, {@code "timestamp cert untrusted: DigiCert SHA256 RSA4096 Timestamp Responder 2025 1"}
     */
    @JsonProperty("explanation")
    private String explanation;

    /**
     * 関連するURL。
     * <p>
     * この検証項目に関連するマニフェスト内のリソースを指すJUMBF URI。
     * <p>
     * 例: {@code "self#jumbf=/c2pa/{manifest_urn}/c2pa.signature"}
     * <p>
     * このURLを使用して、検証対象の具体的な要素を特定できます。
     */
    @JsonProperty("url")
    private String url;
}
