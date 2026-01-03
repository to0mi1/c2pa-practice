package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * サムネイル情報。
 * <p>
 * マニフェストに関連付けられたコンテンツのサムネイル画像に関する情報を保持します。
 * サムネイルは、コンテンツのプレビューとして使用され、マニフェスト内に
 * アサーションとして埋め込まれます。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Thumbnail {

    /**
     * サムネイル画像のフォーマット。
     * <p>
     * サムネイル画像のMIMEタイプ。
     * <p>
     * 例:
     * <ul>
     *   <li>{@code "image/jpeg"} - JPEG形式</li>
     *   <li>{@code "image/png"} - PNG形式</li>
     *   <li>{@code "image/webp"} - WebP形式</li>
     * </ul>
     */
    @JsonProperty("format")
    private String format;

    /**
     * サムネイルの識別子。
     * <p>
     * マニフェスト内でサムネイル画像を参照するためのJUMBF URI。
     * <p>
     * 形式: {@code "self#jumbf=/c2pa/{manifest_urn}/c2pa.assertions/c2pa.thumbnail.claim"}
     * <p>
     * この識別子を使用して、マニフェスト内の実際のサムネイルデータにアクセスできます。
     */
    @JsonProperty("identifier")
    private String identifier;
}
