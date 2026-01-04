package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * AI学習・マイニング制限のアサーション（cawg.training-mining）内のエントリ。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingMiningEntry {

    /**
     * 利用制限の状態。
     * <p>
     * 可能な値: {@code "allowed"}, {@code "notAllowed"}, {@code "constrained"}
     */
    @JsonProperty("use")
    private String use;

    /**
     * 制限の詳細情報。
     * <p>
     * {@code use} が {@code "constrained"} の場合に、具体的な制限内容を記述します。
     */
    @JsonProperty("constraints_info")
    private String constraintsInfo;
}
