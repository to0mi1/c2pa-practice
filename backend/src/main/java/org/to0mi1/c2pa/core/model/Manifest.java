package org.to0mi1.c2pa.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * C2PAマニフェスト。
 * <p>
 * マニフェストは、デジタルコンテンツに関連する証明情報の集合体です。
 * コンテンツの作成者、編集履歴、使用された素材（ingredients）、署名情報などを含みます。
 * 各マニフェストは、コンテンツの特定の状態やバージョンを表します。
 *
 * @see Assertion
 * @see ClaimGeneratorInfo
 * @see SignatureInfo
 * @see Thumbnail
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Manifest {

    /**
     * アサーション（主張）のリスト。
     * <p>
     * アサーションは、コンテンツに関する具体的な主張や情報を表します。
     * 例: 作成アクション、編集履歴、メタデータ、ハッシュ値など。
     * 各アサーションは特定のラベル（例: {@code "c2pa.actions.v2"}）で識別されます。
     */
    @JsonProperty("assertions")
    private List<Assertion> assertions;

    /**
     * クレーム生成ツールの情報リスト。
     * <p>
     * このマニフェストを生成したツールやソフトウェアに関する情報を含みます。
     * ツール名、バージョン、C2PAライブラリのバージョンなどが記録されます。
     */
    @JsonProperty("claim_generator_info")
    private List<ClaimGeneratorInfo> claimGeneratorInfo;

    /**
     * 材料（ingredients）のリスト。
     * <p>
     * このコンテンツの作成に使用された元素材やソースコンテンツを表します。
     * 例: 合成画像の場合、元となった複数の画像が材料として記録されます。
     * 各材料は独自のマニフェストを持つことができ、来歴の連鎖を形成します。
     */
    @JsonProperty("ingredients")
    private List<Object> ingredients;

    /**
     * インスタンスID。
     * <p>
     * このマニフェストインスタンスを一意に識別するID。
     * <p>
     * 形式: {@code "xmp:iid:{UUID}"}（XMP Instance IDの形式に従う）
     */
    @JsonProperty("instance_id")
    private String instanceId;

    /**
     * マニフェストのラベル（URN）。
     * <p>
     * このマニフェストを識別するURN。通常、{@code active_manifest}の値と一致します。
     * <p>
     * 形式: {@code "urn:c2pa:{UUID}"}
     */
    @JsonProperty("label")
    private String label;

    /**
     * 署名情報。
     * <p>
     * このマニフェストに対する暗号署名の詳細情報を含みます。
     * 署名アルゴリズム、証明書情報、署名時刻などが記録され、
     * マニフェストの改ざん検知と作成者の認証に使用されます。
     */
    @JsonProperty("signature_info")
    private SignatureInfo signatureInfo;

    /**
     * サムネイル情報。
     * <p>
     * コンテンツのサムネイル画像に関する情報。
     * サムネイルのフォーマットと、マニフェスト内での参照先を含みます。
     */
    @JsonProperty("thumbnail")
    private Thumbnail thumbnail;

    /**
     * マニフェストのタイトル。
     * <p>
     * このマニフェストまたは関連するコンテンツの人間が読めるタイトル。
     * 任意のフィールドで、コンテンツの識別を容易にします。
     */
    @JsonProperty("title")
    private String title;
}
