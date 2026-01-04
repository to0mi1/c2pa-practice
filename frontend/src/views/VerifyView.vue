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
  <div class="verify-wrapper">
    <div class="manifest">
      <v-container v-if="isLoading" class="d-flex justify-center align-center fill-height">
        <v-progress-circular indeterminate color="primary"></v-progress-circular>
      </v-container>

      <v-alert v-else-if="error" type="error" variant="tonal" class="ma-4">
        {{ error }}
      </v-alert>

      <div v-else-if="verificationResult" class="pa-4">
        <h2 class="text-h5 mb-4">Content Credentials</h2>

        <v-card variant="outlined" class="mb-4">
          <v-card-title class="d-flex align-center">
            <v-icon
                :color="verificationResult.validationState === 'Valid' ? 'success' : 'error'"
                class="mr-2"
            >
              {{ verificationResult.validationState === 'Valid' ? 'mdi-check-circle' : 'mdi-alert-circle' }}
            </v-icon>
            {{ verificationResult.validationState }}
          </v-card-title>
          <v-card-text>
            <div class="mb-2"><strong>Title:</strong> {{ verificationResult.title }}</div>
            <div class="mb-2"><strong>Signer:</strong> {{ verificationResult.signer }}</div>
            <div class="mb-2"><strong>Issuer:</strong> {{ verificationResult.issuer }}</div>
            <div class="mb-2"><strong>Signed At:</strong> {{ new Date(verificationResult.signedAt).toLocaleString() }}</div>
          </v-card-text>
        </v-card>

        <h3 class="text-h6 mb-2">Actions</h3>
        <v-list lines="two" variant="outlined" rounded>
          <v-list-item
              v-for="(action, index) in verificationResult.actions"
              :key="index"
          >
            <v-list-item-title>{{ action.action }}</v-list-item-title>
            <v-list-item-subtitle>{{ action.digitalSourceType }}</v-list-item-subtitle>
          </v-list-item>
        </v-list>

        <div v-if="verificationResult.trainingMining" class="mt-6">
          <h3 class="text-h6 mb-2">AI Use Restrictions</h3>
          <v-card variant="outlined" class="mb-2">
            <v-list lines="three" density="compact">
              <v-list-item v-if="verificationResult.trainingMining.aiInference">
                <v-list-item-title class="font-weight-bold">AI Inference</v-list-item-title>
                <div>
                  <div class="text-body-2">{{ verificationResult.trainingMining.aiInference.use }}</div>
                  <div v-if="verificationResult.trainingMining.aiInference.constraintsInfo" class="text-caption text-grey-darken-1 mt-1">
                    {{ verificationResult.trainingMining.aiInference.constraintsInfo }}
                  </div>
                </div>
              </v-list-item>
              <v-divider v-if="verificationResult.trainingMining.aiInference && verificationResult.trainingMining.aiGenerativeTraining"></v-divider>
              <v-list-item v-if="verificationResult.trainingMining.aiGenerativeTraining">
                <v-list-item-title class="font-weight-bold">AI Generative Training</v-list-item-title>
                <div>
                  <div class="text-body-2">{{ verificationResult.trainingMining.aiGenerativeTraining.use }}</div>
                  <div v-if="verificationResult.trainingMining.aiGenerativeTraining.constraintsInfo" class="text-caption text-grey-darken-1 mt-1">
                    {{ verificationResult.trainingMining.aiGenerativeTraining.constraintsInfo }}
                  </div>
                </div>
              </v-list-item>
            </v-list>
          </v-card>
        </div>

        <div v-if="verificationResult.history && verificationResult.history.length > 0">
          <h3 class="text-h6 mt-6 mb-2">History</h3>
          <v-timeline side="end" density="compact">
            <v-timeline-item
                v-for="(h, index) in verificationResult.history"
                :key="index"
                size="small"
                :dot-color="index === 0 ? 'primary' : 'grey'"
            >
              <div class="d-flex align-center ga-2 mb-1">
                <div class="text-subtitle-2">{{ h.title }}</div>
                <v-chip v-if="index === 0" size="x-small" color="primary" variant="flat">Active</v-chip>
              </div>
              <div class="text-caption">By {{ h.signer }}</div>
              <div class="text-caption">{{ new Date(h.signedAt).toLocaleString() }}</div>
              <div v-if="h.claimGenerator" class="text-caption text-grey">via {{ h.claimGenerator }}</div>
              
              <!-- Actions in history -->
              <div v-if="h.actions && h.actions.length > 0" class="mt-2">
                <div class="text-caption font-weight-bold">Actions:</div>
                <div v-for="(action, aIdx) in h.actions" :key="aIdx" class="text-caption ml-2">
                  • {{ action.action }}
                </div>
              </div>

              <div v-if="h.trainingMining" class="mt-2 pa-2 bg-grey-lighten-4 rounded border">
                <div class="text-caption font-weight-bold mb-1">AI Use Restrictions:</div>
                <div v-if="h.trainingMining.aiInference" class="text-caption d-flex flex-column mb-1">
                  <span>Inference: {{ h.trainingMining.aiInference.use }}</span>
                  <span v-if="h.trainingMining.aiInference.constraintsInfo" class="font-italic text-grey-darken-1 ml-2">
                    - {{ h.trainingMining.aiInference.constraintsInfo }}
                  </span>
                </div>
                <div v-if="h.trainingMining.aiGenerativeTraining" class="text-caption d-flex flex-column">
                  <span>Training: {{ h.trainingMining.aiGenerativeTraining.use }}</span>
                  <span v-if="h.trainingMining.aiGenerativeTraining.constraintsInfo" class="font-italic text-grey-darken-1 ml-2">
                    - {{ h.trainingMining.aiGenerativeTraining.constraintsInfo }}
                  </span>
                </div>
              </div>
            </v-timeline-item>
          </v-timeline>
        </div>
      </div>

      <v-container v-else-if="!selectedFile" class="d-flex justify-center align-center fill-height text-grey">
        Select an image to verify C2PA manifest
      </v-container>
    </div>
    <div class="preview">
      <v-container class="d-flex flex-column ga-4">
        <v-form>
          <v-file-input
              label="File input"
              prepend-icon="mdi-image-outline"
              accept="image/*"
              @update:model-value="onFileChange"
          ></v-file-input>
        </v-form>
        
        <div v-if="previewUrl" class="preview-section">
          <h3 class="text-h6 mb-2">Preview</h3>
          <v-img
              :src="previewUrl"
              max-height="600"
              class="align-self-start border rounded"
              width="100%"
          ></v-img>
        </div>
      </v-container>
    </div>
  </div>
</template>

<style scoped lang="scss">
.verify-wrapper {
  display: grid;
  grid-template-columns: 350px 1fr;
  height: calc(100vh - 64px); /* Adjust based on your header height */
}

.manifest {
  border-right: 1px solid rgba(0, 0, 0, 0.12);
  overflow-y: auto;
}

.preview {
  overflow-y: auto;
}

.preview-section {
  width: 100%;
}
</style>