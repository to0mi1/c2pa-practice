<script setup lang="ts">
import { ref } from 'vue'

/**
 * 署名フォームの入力データ
 */
const title = ref('')
const aiInference = ref('allowed')
const aiInferenceConstraintsInfo = ref('')
const aiGenerativeTraining = ref('allowed')
const aiGenerativeTrainingConstraintsInfo = ref('')

/**
 * AI利用制限の選択肢
 */
const aiOptions = [
  { title: 'Allowed', value: 'allowed' },
  { title: 'Not Allowed', value: 'notAllowed' },
  { title: 'Constrained', value: 'constrained' },
]

/**
 * 状態管理
 */
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)

/**
 * ファイル選択時の処理
 * プレビューURLの生成と古いURLの解放を行う
 */
const onFileChange = (file: File | File[] | null) => {
  const actualFile = Array.isArray(file) ? file[0] : file
  
  // 既存のプレビューURLを解放
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = null
  }

  if (!actualFile) {
    selectedFile.value = null
    return
  }

  selectedFile.value = actualFile
  previewUrl.value = URL.createObjectURL(actualFile)
}

/**
 * 署名実行処理
 * 1. フォームデータを構築
 * 2. バックエンドAPIに送信
 * 3. 署名済み画像をダウンロード
 * 4. プレビューを更新
 */
const onSign = async () => {
  if (!selectedFile.value || !title.value) return

  isLoading.value = true
  error.value = null

  const formData = new FormData()
  formData.append('title', title.value)
  formData.append('image', selectedFile.value)
  formData.append('ai_inference', aiInference.value)
  formData.append('ai_inference_constraints_info', aiInferenceConstraintsInfo.value)
  formData.append('ai_generative_training', aiGenerativeTraining.value)
  formData.append('ai_generative_training_constraints_info', aiGenerativeTrainingConstraintsInfo.value)

  try {
    const response = await fetch('/api/c2pa/sign', {
      method: 'POST',
      body: formData,
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `Signing failed: ${response.statusText}`)
    }

    const blob = await response.blob()
    const url = URL.createObjectURL(blob)

    // 署名済み画像のダウンロード用リンクを作成
    const link = document.createElement('a')
    link.href = url
    
    // 元のファイル名に "_signed" を付与
    const originalName = selectedFile.value.name
    const signedName = originalName.replace(/(\.[\w\d]+)$/, '_signed$1') || 'signed_image.jpg'
    link.download = signedName
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    // プレビューを署名済み画像で更新
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
    }
    previewUrl.value = url
  } catch (err) {
    console.error('Error signing image:', err)
    error.value = err instanceof Error ? err.message : 'An unknown error occurred'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <v-row>
    <v-col cols="12" md="6">
      <v-card elevation="2" rounded="lg">
        <v-card-item>
          <template v-slot:prepend>
            <v-icon icon="mdi-fountain-pen-tip" color="primary"></v-icon>
          </template>
          <v-card-title class="text-h5 font-weight-bold">Sign Asset</v-card-title>
          <v-card-subtitle>Attach a C2PA manifest to your image</v-card-subtitle>
        </v-card-item>

        <v-divider></v-divider>

        <v-card-text>
          <v-form @submit.prevent="onSign">
            <v-text-field
                v-model="title"
                label="Asset Title"
                placeholder="e.g. My Artwork"
                variant="outlined"
                density="comfortable"
                required
                prepend-inner-icon="mdi-format-title"
                class="mb-2"
            ></v-text-field>

            <v-row dense>
              <v-col cols="12" sm="6">
                <v-select
                    v-model="aiInference"
                    :items="aiOptions"
                    label="AI Inference"
                    variant="outlined"
                    density="comfortable"
                    required
                    prepend-inner-icon="mdi-robot"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                    v-model="aiGenerativeTraining"
                    :items="aiOptions"
                    label="AI Generative Training"
                    variant="outlined"
                    density="comfortable"
                    required
                    prepend-inner-icon="mdi-brain"
                ></v-select>
              </v-col>
            </v-row>

            <v-expand-transition>
              <div v-if="aiInference === 'constrained' || aiGenerativeTraining === 'constrained'">
                <v-row dense>
                  <v-col v-if="aiInference === 'constrained'" cols="12">
                    <v-text-field
                        v-model="aiInferenceConstraintsInfo"
                        label="Inference Constraints Info"
                        placeholder="Details about inference constraints"
                        variant="outlined"
                        density="comfortable"
                        class="mb-2"
                    ></v-text-field>
                  </v-col>
                  <v-col v-if="aiGenerativeTraining === 'constrained'" cols="12">
                    <v-text-field
                        v-model="aiGenerativeTrainingConstraintsInfo"
                        label="Generative Training Constraints Info"
                        placeholder="Details about training constraints"
                        variant="outlined"
                        density="comfortable"
                        class="mb-2"
                    ></v-text-field>
                  </v-col>
                </v-row>
              </div>
            </v-expand-transition>

            <v-file-input
                label="Select image to sign"
                placeholder="Click to upload"
                prepend-inner-icon="mdi-image-plus-outline"
                prepend-icon=""
                accept="image/*"
                required
                show-size
                variant="outlined"
                density="comfortable"
                @update:model-value="onFileChange"
                class="mb-4"
            ></v-file-input>

            <v-btn
                color="primary"
                type="submit"
                size="large"
                block
                elevation="2"
                :loading="isLoading"
                :disabled="!selectedFile || !title"
                prepend-icon="mdi-content-save-check-outline"
            >
              Sign and Download
            </v-btn>
          </v-form>
        </v-card-text>
      </v-card>

      <v-alert
          v-if="error"
          type="error"
          variant="tonal"
          closable
          class="mt-4"
          @click:close="error = null"
      >
        {{ error }}
      </v-alert>
    </v-col>

    <v-col cols="12" md="6">
      <v-card v-if="previewUrl" elevation="2" rounded="lg" class="fill-height d-flex flex-column">
        <v-card-item>
          <template v-slot:prepend>
            <v-icon icon="mdi-eye-outline" color="secondary"></v-icon>
          </template>
          <v-card-title class="text-h5 font-weight-bold">Preview</v-card-title>
        </v-card-item>
        <v-divider></v-divider>
        <v-card-text class="flex-grow-1 d-flex align-center justify-center bg-grey-lighten-5">
          <v-img
              :src="previewUrl"
              max-height="600"
              class="rounded-lg shadow-sm"
              contain>
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
          style="border-style: dashed !important; border-width: 2px !important;"
      >
        <v-icon size="64" icon="mdi-image-off-outline" class="mb-4 text-grey-lighten-1"></v-icon>
        <div class="text-h6">No image selected</div>
        <div class="text-body-2">Select an image to see the preview</div>
      </v-sheet>
    </v-col>
  </v-row>
</template>

<style scoped>
.preview-section {
  width: 100%;
}
</style>