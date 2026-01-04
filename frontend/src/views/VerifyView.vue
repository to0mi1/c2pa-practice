<script setup lang="ts">
import { ref } from 'vue'

/**
 * アクション情報の型定義
 */
interface ActionInfo {
  action: string
  digitalSourceType: string
}

/**
 * AI学習制限エントリの型定義
 */
interface TrainingMiningEntry {
  use: string
  constraintsInfo?: string
}

/**
 * AI学習制限情報の型定義
 */
interface TrainingMiningInfo {
  aiInference?: TrainingMiningEntry
  aiGenerativeTraining?: TrainingMiningEntry
}

/**
 * マニフェスト履歴の型定義
 */
interface ManifestHistory {
  label: string
  title: string
  signer: string
  issuer: string
  signedAt: string
  algorithm: string
  actions: ActionInfo[]
  trainingMining?: TrainingMiningInfo
  claimGenerator: string
}

/**
 * 検証結果レスポンスの型定義
 */
interface VerifyResponse {
  title: string
  signer: string
  issuer: string
  signedAt: string
  algorithm: string
  actions: ActionInfo[]
  trainingMining?: TrainingMiningInfo
  validationState: string
  history: ManifestHistory[]
}

/**
 * 状態管理
 */
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string | null>(null)
const verificationResult = ref<VerifyResponse | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)
const activePanels = ref([0, 1, 2])

/**
 * AI利用制限のチップの色を取得
 */
const getAiUseColor = (use: string) => {
  switch (use) {
    case 'allowed': return 'success'
    case 'notAllowed': return 'error'
    case 'constrained': return 'warning'
    default: return 'grey'
  }
}

/**
 * 画像検証処理
 * 指定されたファイルをバックエンドの検証APIに送信する
 */
const verifyImage = async (file: File) => {
  isLoading.value = true
  error.value = null
  verificationResult.value = null

  const formData = new FormData()
  formData.append('image', file)

  try {
    const response = await fetch('/api/c2pa/verify', {
      method: 'POST',
      body: formData,
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `Verification failed: ${response.statusText}`)
    }

    verificationResult.value = await response.json()
  } catch (err) {
    console.error('Error verifying image:', err)
    error.value = err instanceof Error ? err.message : 'An unknown error occurred'
  } finally {
    isLoading.value = false
  }
}

/**
 * ファイル選択時の処理
 * プレビュー表示と検証処理のトリガー
 */
const onFileChange = (file: File | File[] | null) => {
  // 古いプレビューURLを解放
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = null
  }

  const actualFile = Array.isArray(file) ? file[0] : file
  if (actualFile) {
    selectedFile.value = actualFile
    previewUrl.value = URL.createObjectURL(actualFile)
    verifyImage(actualFile)
  } else {
    selectedFile.value = null
    verificationResult.value = null
    error.value = null
  }
}
</script>

<template>
  <v-row>
    <!-- Left Column: Input and Preview -->
    <v-col cols="12" md="7">
      <v-card elevation="2" rounded="lg" class="mb-4">
        <v-card-item>
          <template v-slot:prepend>
            <v-icon icon="mdi-magnify-expand" color="primary"></v-icon>
          </template>
          <v-card-title class="text-h5 font-weight-bold">Inspect Asset</v-card-title>
          <v-card-subtitle>Verify the origin and history of an image</v-card-subtitle>
        </v-card-item>
        <v-divider></v-divider>
        <v-card-text>
          <v-file-input
              label="Select image to verify"
              placeholder="Click to upload"
              prepend-inner-icon="mdi-image-search-outline"
              prepend-icon=""
              accept="image/*"
              show-size
              variant="outlined"
              density="comfortable"
              class="mb-0"
              @update:model-value="onFileChange"
          ></v-file-input>
        </v-card-text>
      </v-card>

      <v-card v-if="previewUrl" elevation="2" rounded="lg">
        <v-card-text class="pa-2 bg-grey-lighten-5 rounded-lg">
          <v-img
              :src="previewUrl"
              max-height="600"
              class="rounded-lg"
              contain
          >
            <template v-slot:placeholder>
              <div class="d-flex align-center justify-center fill-height">
                <v-progress-circular indeterminate color="primary"></v-progress-circular>
              </div>
            </template>
          </v-img>
        </v-card-text>
      </v-card>

      <v-sheet
          v-else
          rounded="lg"
          border
          class="fill-height d-flex flex-column align-center justify-center text-grey-darken-1 py-12"
          color="transparent"
          style="border-style: dashed !important; border-width: 2px !important; min-height: 400px;"
      >
        <v-icon size="64" icon="mdi-shield-search" class="mb-4 text-grey-lighten-1"></v-icon>
        <div class="text-h6">Ready to Verify</div>
        <div class="text-body-2">Upload an image to check its Content Credentials</div>
      </v-sheet>
    </v-col>

    <!-- Right Column: Verification Results -->
    <v-col cols="12" md="5">
      <v-fade-transition mode="out-in">
        <div v-if="isLoading" class="d-flex flex-column align-center justify-center py-12">
          <v-progress-circular indeterminate color="primary" size="64" width="6" class="mb-4"></v-progress-circular>
          <div class="text-h6 text-primary">Analyzing Manifest...</div>
        </div>

        <v-alert
            v-else-if="error"
            type="error"
            variant="tonal"
            icon="mdi-alert-octagon"
            title="Verification Error"
            class="rounded-lg"
        >
          {{ error }}
        </v-alert>

        <div v-else-if="verificationResult">
          <!-- Summary Card -->
          <v-card elevation="4" rounded="lg" border :color="verificationResult.validationState === 'Valid' ? 'success' : 'warning'" variant="tonal" class="mb-4">
            <v-card-item>
              <template v-slot:prepend>
                <v-icon
                    :icon="verificationResult.validationState === 'Valid' ? 'mdi-check-decagram' : 'mdi-alert-decagram'"
                    size="large"
                ></v-icon>
              </template>
              <v-card-title class="text-h6 font-weight-bold">
                {{ verificationResult.validationState === 'Valid' ? 'Content Credentials Found' : 'Validation Issues' }}
              </v-card-title>
            </v-card-item>
            <v-card-text class="pt-0">
              <div class="text-body-2 mb-2">
                This image contains C2PA metadata that has been {{ verificationResult.validationState.toLowerCase() }}ly verified.
              </div>
            </v-card-text>
          </v-card>

          <!-- Details Accordion -->
          <v-expansion-panels v-model="activePanels" multiple class="mb-4">
            <!-- Basic Info -->
            <v-expansion-panel elevation="1" rounded="lg">
              <v-expansion-panel-title class="font-weight-bold">
                <v-icon icon="mdi-information-outline" class="me-2" color="primary"></v-icon>
                General Information
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <v-list density="compact">
                  <v-list-item class="px-0">
                    <template v-slot:prepend>
                      <v-icon icon="mdi-label-outline" size="small" class="me-2"></v-icon>
                    </template>
                    <v-list-item-subtitle>Title</v-list-item-subtitle>
                    <v-list-item-title class="font-weight-medium">{{ verificationResult.title }}</v-list-item-title>
                  </v-list-item>
                  <v-list-item class="px-0">
                    <template v-slot:prepend>
                      <v-icon icon="mdi-account-check-outline" size="small" class="me-2"></v-icon>
                    </template>
                    <v-list-item-subtitle>Signer</v-list-item-subtitle>
                    <v-list-item-title class="font-weight-medium">{{ verificationResult.signer }}</v-list-item-title>
                  </v-list-item>
                  <v-list-item class="px-0">
                    <template v-slot:prepend>
                      <v-icon icon="mdi-office-building-outline" size="small" class="me-2"></v-icon>
                    </template>
                    <v-list-item-subtitle>Issuer</v-list-item-subtitle>
                    <v-list-item-title class="font-weight-medium">{{ verificationResult.issuer }}</v-list-item-title>
                  </v-list-item>
                  <v-list-item class="px-0">
                    <template v-slot:prepend>
                      <v-icon icon="mdi-clock-outline" size="small" class="me-2"></v-icon>
                    </template>
                    <v-list-item-subtitle>Signed Date</v-list-item-subtitle>
                    <v-list-item-title class="font-weight-medium">
                      {{ new Date(verificationResult.signedAt).toLocaleString() }}
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-expansion-panel-text>
            </v-expansion-panel>

            <!-- Actions -->
            <v-expansion-panel elevation="1">
              <v-expansion-panel-title class="font-weight-bold">
                <v-icon icon="mdi-history" class="me-2" color="secondary"></v-icon>
                Actions Taken
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <v-chip-group column>
                  <v-chip
                      v-for="(action, index) in verificationResult.actions"
                      :key="index"
                      size="small"
                      variant="flat"
                      color="blue-lighten-5"
                      text-color="blue-darken-2"
                      class="mb-1"
                  >
                    {{ action.action.replace('c2pa.', '') }}
                  </v-chip>
                </v-chip-group>
                <v-list density="compact" class="mt-2">
                  <v-list-item
                      v-for="(action, index) in verificationResult.actions"
                      :key="index"
                      :title="action.action"
                      :subtitle="action.digitalSourceType"
                      class="px-0"
                  >
                    <template v-slot:prepend>
                      <v-icon icon="mdi-circle-medium" color="grey"></v-icon>
                    </template>
                  </v-list-item>
                </v-list>
              </v-expansion-panel-text>
            </v-expansion-panel>

            <!-- AI Restrictions -->
            <v-expansion-panel v-if="verificationResult.trainingMining" elevation="1">
              <v-expansion-panel-title class="font-weight-bold">
                <v-icon icon="mdi-robot-confused-outline" class="me-2" color="orange-darken-2"></v-icon>
                AI Use Restrictions
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <div v-if="verificationResult.trainingMining.aiInference" class="mb-4">
                  <div class="d-flex align-center mb-1">
                    <span class="text-subtitle-2 font-weight-bold">AI Inference</span>
                    <v-spacer></v-spacer>
                    <v-chip
                        size="x-small"
                        :color="getAiUseColor(verificationResult.trainingMining.aiInference.use)"
                        variant="flat"
                    >
                      {{ verificationResult.trainingMining.aiInference.use }}
                    </v-chip>
                  </div>
                  <div v-if="verificationResult.trainingMining.aiInference.constraintsInfo" class="text-caption pa-2 bg-grey-lighten-4 rounded">
                    {{ verificationResult.trainingMining.aiInference.constraintsInfo }}
                  </div>
                </div>

                <div v-if="verificationResult.trainingMining.aiGenerativeTraining">
                  <div class="d-flex align-center mb-1">
                    <span class="text-subtitle-2 font-weight-bold">Generative Training</span>
                    <v-spacer></v-spacer>
                    <v-chip
                        size="x-small"
                        :color="getAiUseColor(verificationResult.trainingMining.aiGenerativeTraining.use)"
                        variant="flat"
                    >
                      {{ verificationResult.trainingMining.aiGenerativeTraining.use }}
                    </v-chip>
                  </div>
                  <div v-if="verificationResult.trainingMining.aiGenerativeTraining.constraintsInfo" class="text-caption pa-2 bg-grey-lighten-4 rounded">
                    {{ verificationResult.trainingMining.aiGenerativeTraining.constraintsInfo }}
                  </div>
                </div>
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>

          <!-- History Timeline -->
          <v-card v-if="verificationResult.history && verificationResult.history.length > 0" elevation="2" rounded="lg">
            <v-card-item>
              <v-card-title class="text-subtitle-1 font-weight-bold">Provenance History</v-card-title>
            </v-card-item>
            <v-divider></v-divider>
            <v-card-text>
              <v-timeline side="end" density="compact" align="start">
                <v-timeline-item
                    v-for="(h, index) in verificationResult.history"
                    :key="index"
                    size="x-small"
                    :dot-color="index === 0 ? 'primary' : 'grey-lighten-1'"
                >
                  <div class="d-flex flex-column ga-1">
                    <div class="d-flex align-center ga-2">
                      <span class="text-subtitle-2 font-weight-bold">{{ h.title }}</span>
                      <v-chip v-if="index === 0" size="x-small" color="primary" variant="flat">Current</v-chip>
                    </div>
                    <div class="text-caption text-grey-darken-1">
                      <v-icon icon="mdi-account" size="12" class="me-1"></v-icon>{{ h.signer }}
                    </div>
                    <div class="text-caption text-grey">
                      <v-icon icon="mdi-calendar" size="12" class="me-1"></v-icon>{{ new Date(h.signedAt).toLocaleString() }}
                    </div>
                    
                    <v-divider v-if="h.actions && h.actions.length > 0" class="my-1"></v-divider>
                    
                    <div v-if="h.actions && h.actions.length > 0" class="d-flex flex-wrap ga-1">
                      <span v-for="(action, aIdx) in h.actions" :key="aIdx" class="text-xxs px-1 bg-grey-lighten-3 rounded text-grey-darken-2">
                        {{ action.action.split('.').pop() }}
                      </span>
                    </div>
                  </div>
                </v-timeline-item>
              </v-timeline>
            </v-card-text>
          </v-card>
        </div>

        <v-sheet
            v-else
            rounded="lg"
            variant="outlined"
            class="d-flex flex-column align-center justify-center text-grey text-center py-12 px-6"
            style="border-style: dashed !important;"
        >
          <v-icon size="48" icon="mdi-file-find-outline" class="mb-2 opacity-50"></v-icon>
          <div class="text-body-1 font-weight-medium">Results will appear here</div>
          <div class="text-caption">Verification data and provenance history will be displayed after analysis</div>
        </v-sheet>
      </v-fade-transition>
    </v-col>
  </v-row>
</template>

<style scoped lang="scss">
.shadow-sm {
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.text-xxs {
  font-size: 0.65rem;
  line-height: 1rem;
}
</style>