package org.to0mi1.c2pa.application.mapper;

import org.springframework.stereotype.Component;
import org.to0mi1.c2pa.application.dto.VerifyResponse;
import org.to0mi1.c2pa.core.model.Action;
import org.to0mi1.c2pa.core.model.Assertion;
import org.to0mi1.c2pa.core.model.C2paManifest;
import org.to0mi1.c2pa.core.model.Manifest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * {@link C2paManifest}から{@link VerifyResponse}への変換を行うマッパークラス。
 * <p>
 * C2PA検証結果の生データから、クライアントに返すレスポンスDTOへの変換ロジックを提供します。
 * コントローラーの肥大化を防ぎ、変換ロジックの再利用性とテスタビリティを向上させます。
 *
 * @see C2paManifest
 * @see VerifyResponse
 */
@Component
public class VerifyResponseMapper {

    private static final String ACTIONS_LABEL = "c2pa.actions.v2";
    private static final String TRAINING_MINING_LABEL = "cawg.training-mining";

    /**
     * {@link C2paManifest}を{@link VerifyResponse}に変換します。
     *
     * @param c2paManifest C2PA検証結果
     * @return 変換されたレスポンスDTO
     */
    public VerifyResponse toVerifyResponse(C2paManifest c2paManifest) {
        VerifyResponse response = new VerifyResponse();
        response.setValidationState(c2paManifest.getValidationState());

        if (c2paManifest.getActiveManifest() != null && c2paManifest.getManifests() != null) {
            Manifest activeManifest = c2paManifest.getManifests().get(c2paManifest.getActiveManifest());
            if (activeManifest != null) {
                populateActiveManifestInfo(response, activeManifest);
            }

            List<VerifyResponse.ManifestHistory> history = buildManifestHistory(c2paManifest);
            response.setHistory(history);
        }

        return response;
    }

    /**
     * アクティブなマニフェストの情報をレスポンスに設定します。
     *
     * @param response レスポンスDTO
     * @param manifest アクティブなマニフェスト
     */
    private void populateActiveManifestInfo(VerifyResponse response, Manifest manifest) {
        response.setTitle(manifest.getTitle());

        if (manifest.getSignatureInfo() != null) {
            response.setSigner(manifest.getSignatureInfo().getCommonName());
            response.setIssuer(manifest.getSignatureInfo().getIssuer());
            response.setSignedAt(manifest.getSignatureInfo().getTime());
            response.setAlgorithm(manifest.getSignatureInfo().getAlg());
        }

        List<VerifyResponse.ActionInfo> actions = extractActions(manifest);
        response.setActions(actions);

        VerifyResponse.TrainingMiningInfo trainingMining = extractTrainingMining(manifest);
        response.setTrainingMining(trainingMining);
    }

    /**
     * マニフェストからAI学習・マイニング制限情報を抽出します。
     *
     * @param manifest マニフェスト
     * @return AI学習・マイニング制限情報
     */
    private VerifyResponse.TrainingMiningInfo extractTrainingMining(Manifest manifest) {
        if (manifest.getAssertions() == null) {
            return null;
        }

        for (Assertion assertion : manifest.getAssertions()) {
            if (TRAINING_MINING_LABEL.equals(assertion.getLabel()) && assertion.getData() != null) {
                if (assertion.getData().getEntries() != null) {
                    VerifyResponse.TrainingMiningInfo info = new VerifyResponse.TrainingMiningInfo();
                    
                    org.to0mi1.c2pa.core.model.TrainingMiningEntry inferenceEntry = 
                            assertion.getData().getEntries().get("c2pa.ai_inference");
                    if (inferenceEntry != null) {
                        VerifyResponse.TrainingMiningEntry entry = new VerifyResponse.TrainingMiningEntry();
                        entry.setUse(inferenceEntry.getUse());
                        entry.setConstraintsInfo(inferenceEntry.getConstraintsInfo());
                        info.setAiInference(entry);
                    }

                    org.to0mi1.c2pa.core.model.TrainingMiningEntry trainingEntry = 
                            assertion.getData().getEntries().get("c2pa.ai_generative_training");
                    if (trainingEntry != null) {
                        VerifyResponse.TrainingMiningEntry entry = new VerifyResponse.TrainingMiningEntry();
                        entry.setUse(trainingEntry.getUse());
                        entry.setConstraintsInfo(trainingEntry.getConstraintsInfo());
                        info.setAiGenerativeTraining(entry);
                    }
                    
                    return info;
                }
            }
        }

        return null;
    }

    /**
     * マニフェストからアクション情報を抽出します。
     *
     * @param manifest マニフェスト
     * @return アクション情報のリスト
     */
    private List<VerifyResponse.ActionInfo> extractActions(Manifest manifest) {
        List<VerifyResponse.ActionInfo> actions = new ArrayList<>();

        if (manifest.getAssertions() == null) {
            return actions;
        }

        for (Assertion assertion : manifest.getAssertions()) {
            if (ACTIONS_LABEL.equals(assertion.getLabel()) && assertion.getData() != null) {
                if (assertion.getData().getActions() != null) {
                    for (Action action : assertion.getData().getActions()) {
                        VerifyResponse.ActionInfo actionInfo = new VerifyResponse.ActionInfo();
                        actionInfo.setAction(action.getAction());
                        actionInfo.setDigitalSourceType(action.getDigitalSourceType());
                        actions.add(actionInfo);
                    }
                }
            }
        }

        return actions;
    }

    /**
     * すべてのマニフェストから署名履歴を構築します。
     *
     * @param c2paManifest C2PAマニフェスト
     * @return 署名履歴のリスト（署名時刻の降順）
     */
    private List<VerifyResponse.ManifestHistory> buildManifestHistory(C2paManifest c2paManifest) {
        List<VerifyResponse.ManifestHistory> history = new ArrayList<>();

        for (Manifest manifest : c2paManifest.getManifests().values()) {
            VerifyResponse.ManifestHistory historyItem = createManifestHistory(manifest);
            history.add(historyItem);
        }

        sortHistoryBySignedDate(history);
        return history;
    }

    /**
     * マニフェストから履歴項目を作成します。
     *
     * @param manifest マニフェスト
     * @return マニフェスト履歴項目
     */
    private VerifyResponse.ManifestHistory createManifestHistory(Manifest manifest) {
        VerifyResponse.ManifestHistory historyItem = new VerifyResponse.ManifestHistory();
        historyItem.setLabel(manifest.getLabel());
        historyItem.setTitle(manifest.getTitle());

        if (manifest.getSignatureInfo() != null) {
            historyItem.setSigner(manifest.getSignatureInfo().getCommonName());
            historyItem.setIssuer(manifest.getSignatureInfo().getIssuer());
            historyItem.setSignedAt(manifest.getSignatureInfo().getTime());
            historyItem.setAlgorithm(manifest.getSignatureInfo().getAlg());
        }

        if (manifest.getClaimGeneratorInfo() != null && !manifest.getClaimGeneratorInfo().isEmpty()) {
            historyItem.setClaimGenerator(manifest.getClaimGeneratorInfo().get(0).getName());
        }

        List<VerifyResponse.ActionInfo> actions = extractActions(manifest);
        historyItem.setActions(actions);

        VerifyResponse.TrainingMiningInfo trainingMining = extractTrainingMining(manifest);
        historyItem.setTrainingMining(trainingMining);

        return historyItem;
    }

    /**
     * 履歴を署名時刻で降順にソートします（最新が先頭）。
     *
     * @param history 署名履歴のリスト
     */
    private void sortHistoryBySignedDate(List<VerifyResponse.ManifestHistory> history) {
        history.sort(Comparator.comparing(
                VerifyResponse.ManifestHistory::getSignedAt,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
    }
}
