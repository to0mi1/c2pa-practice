package org.to0mi1.c2pa.application.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 検証結果のレスポンスDTO。
 * <p>
 * C2PA検証結果から主要な項目を抜粋して返します。
 * 誰が、何の操作を、いつ署名したかを明確に示します。
 */
@Data
public class VerifyResponse {

    /**
     * マニフェストのタイトル。
     */
    private String title;

    /**
     * 署名者の名前（コモンネーム）。
     */
    private String signer;

    /**
     * 証明書の発行者。
     */
    private String issuer;

    /**
     * 署名時刻。
     */
    private ZonedDateTime signedAt;

    /**
     * 署名アルゴリズム。
     */
    private String algorithm;

    /**
     * 実行されたアクションのリスト。
     */
    private List<ActionInfo> actions;

    /**
     * 検証状態。
     * <p>
     * 可能な値: {@code "Valid"}, {@code "Invalid"}
     */
    private String validationState;

    /**
     * 過去の署名履歴のリスト。
     * <p>
     * 画像が複数回署名されている場合、各署名の履歴を時系列順に保持します。
     * 最新の署名が先頭に、最も古い署名が末尾になります。
     */
    private List<ManifestHistory> history;

    /**
     * アクション情報。
     *
     * @see org.to0mi1.c2pa.core.model.Action
     */
    @Data
    public static class ActionInfo {

        /**
         * アクションの種類。
         * <p>
         * 例: {@code "c2pa.created"}, {@code "c2pa.edited"}
         */
        private String action;

        /**
         * デジタルソースタイプ。
         */
        private String digitalSourceType;
    }

    /**
     * マニフェスト履歴情報。
     * <p>
     * 過去の各署名に関する情報を保持します。
     */
    @Data
    public static class ManifestHistory {

        /**
         * マニフェストのラベル（URN）。
         */
        private String label;

        /**
         * マニフェストのタイトル。
         */
        private String title;

        /**
         * 署名者の名前（コモンネーム）。
         */
        private String signer;

        /**
         * 証明書の発行者。
         */
        private String issuer;

        /**
         * 署名時刻。
         */
        private ZonedDateTime signedAt;

        /**
         * 署名アルゴリズム。
         */
        private String algorithm;

        /**
         * 実行されたアクションのリスト。
         */
        private List<ActionInfo> actions;

        /**
         * クレーム生成ツールの情報。
         */
        private String claimGenerator;
    }
}
