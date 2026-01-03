package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * アサーションデータ。
 * <p>
 * アサーションの実際の内容を保持するクラスです。
 * このクラスは、特にアクション履歴アサーション（{@code "c2pa.actions.v2"}）のデータ構造を表現します。
 * アサーションの種類によって、含まれるフィールドは異なる場合があります。
 *
 * @see Action
 * @see Assertion
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssertionData {

    /**
     * アクションのリスト。
     * <p>
     * コンテンツに対して実行されたアクション（操作）の履歴を表します。
     * 各アクションは、作成、編集、変換などの操作を記録し、
     * コンテンツの来歴を追跡可能にします。
     */
    @JsonProperty("actions")
    private List<Action> actions;
}
