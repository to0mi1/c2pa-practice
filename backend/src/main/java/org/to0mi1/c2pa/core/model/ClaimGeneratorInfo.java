package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * クレーム生成ツール情報。
 * <p>
 * C2PAマニフェストを生成したソフトウェアやツールに関する情報を保持します。
 * この情報により、マニフェストの作成元を追跡し、使用されたツールのバージョンを
 * 確認することができます。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimGeneratorInfo {

    /**
     * ツールまたはソフトウェアの名前。
     * <p>
     * マニフェストを生成したツールやアプリケーションの名前。
     * <p>
     * 例: {@code "Adobe Photoshop"}, {@code "C2PA Example"}, {@code "Custom Tool"}など。
     */
    @JsonProperty("name")
    private String name;

    /**
     * C2PA Rustライブラリのバージョン。
     * <p>
     * マニフェスト生成に使用されたC2PA Rust実装（{@code c2pa-rs}）のバージョン番号。
     * このフィールドは、C2PA Rustライブラリを使用するツールで設定されます。
     * <p>
     * 例: {@code "0.67.1"}
     */
    @JsonProperty("org.contentauth.c2pa_rs")
    private String c2paRsVersion;

    /**
     * ツールのバージョン。
     * <p>
     * マニフェストを生成したツールやソフトウェア自体のバージョン番号。
     * <p>
     * 例: {@code "0.0.1"}, {@code "24.5.0"}など。
     */
    @JsonProperty("version")
    private String version;
}
