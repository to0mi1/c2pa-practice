package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * アサーション（主張）。
 * <p>
 * アサーションは、コンテンツに関する特定の主張や情報を表すC2PAの基本単位です。
 * 各アサーションは、ラベルによって識別され、そのラベルに応じた構造化データを保持します。
 * <p>
 * 例: アクション履歴（{@code "c2pa.actions.v2"}）、ハッシュデータ（{@code "c2pa.hash.data"}）、
 * サムネイル（{@code "c2pa.thumbnail.claim"}）など。
 *
 * @see AssertionData
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Assertion {

    /**
     * アサーションのデータ。
     * <p>
     * アサーションの実際の内容を保持します。
     * データの構造はアサーションのラベルによって異なります。
     * <p>
     * 例: アクション履歴の場合、実行されたアクションのリストを含みます。
     */
    @JsonProperty("data")
    private AssertionData data;

    /**
     * アサーションのラベル。
     * <p>
     * このアサーションの種類を識別するラベル。
     * C2PA標準で定義されたラベル（例: {@code "c2pa.actions.v2"}, {@code "c2pa.hash.data"}）や、
     * カスタムラベルを使用できます。
     */
    @JsonProperty("label")
    private String label;
}
