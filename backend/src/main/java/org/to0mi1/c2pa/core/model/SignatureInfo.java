package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 署名情報。
 * <p>
 * C2PAマニフェストに対する暗号署名の詳細情報を保持します。
 * 署名により、マニフェストの完全性（改ざんされていないこと）と
 * 作成者の真正性（署名者の身元）を検証できます。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureInfo {

    /**
     * 署名アルゴリズム。
     * <p>
     * 使用された暗号署名アルゴリズムの識別子。
     * <p>
     * 例:
     * <ul>
     *   <li>{@code "Es256"} - ECDSA with SHA-256（楕円曲線デジタル署名アルゴリズム）</li>
     *   <li>{@code "Es384"} - ECDSA with SHA-384</li>
     *   <li>{@code "Es512"} - ECDSA with SHA-512</li>
     *   <li>{@code "Ps256"} - RSASSA-PSS with SHA-256</li>
     * </ul>
     */
    @JsonProperty("alg")
    private String alg;

    /**
     * 証明書のシリアル番号。
     * <p>
     * 署名に使用されたX.509証明書の一意のシリアル番号。
     * この番号により、証明書を一意に識別できます。
     */
    @JsonProperty("cert_serial_number")
    private String certSerialNumber;

    /**
     * コモンネーム（CN）。
     * <p>
     * 署名証明書のサブジェクトのコモンネーム。
     * 通常、署名者の名前または組織名を表します。
     * <p>
     * 例: {@code "C2PA Signer"}, {@code "Adobe Systems"}など。
     */
    @JsonProperty("common_name")
    private String commonName;

    /**
     * 発行者。
     * <p>
     * 署名証明書を発行した認証局（CA）の名前。
     * <p>
     * 例: {@code "C2PA Test Signing Cert"}, {@code "DigiCert"}など。
     */
    @JsonProperty("issuer")
    private String issuer;

    /**
     * 署名時刻。
     * <p>
     * マニフェストが署名された日時。
     * タイムゾーン情報を含みます。
     * <p>
     * 例: 2026-01-02T10:14:46+00:00
     */
    @JsonProperty("time")
    private ZonedDateTime time;
}
