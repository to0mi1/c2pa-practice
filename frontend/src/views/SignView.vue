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
  <v-container class="d-flex flex-column ga-4">
    <div class="sign-form">
      <v-form @submit.prevent="onSign">
        <v-text-field
            v-model="title"
            label="Title"
            required
        ></v-text-field>

        <v-row>
          <v-col cols="12" sm="6">
            <v-select
                v-model="aiInference"
                :items="aiOptions"
                label="AI Inference"
                required
            ></v-select>
            <v-text-field
                v-if="aiInference === 'constrained'"
                v-model="aiInferenceConstraintsInfo"
                label="Inference Constraints Info"
                placeholder="Enter details about inference constraints"
            ></v-text-field>
          </v-col>
          <v-col cols="12" sm="6">
            <v-select
                v-model="aiGenerativeTraining"
                :items="aiOptions"
                label="AI Generative Training"
                required
            ></v-select>
            <v-text-field
                v-if="aiGenerativeTraining === 'constrained'"
                v-model="aiGenerativeTrainingConstraintsInfo"
                label="Generative Training Constraints Info"
                placeholder="Enter details about training constraints"
            ></v-text-field>
          </v-col>
        </v-row>

        <v-file-input
            label="Select image to sign"
            prepend-icon="mdi-image-outline"
            accept="image/*"
            required
            show-size
            @update:model-value="onFileChange"
        ></v-file-input>
        <div class="d-flex justify-end align-center ga-4">
          <v-btn
              color="primary"
              type="submit"
              size="large"
              :loading="isLoading"
              :disabled="!selectedFile || !title"
          >Sign and Download</v-btn>
        </div>
      </v-form>
    </div>

    <v-alert v-if="error" type="error" variant="tonal">
      {{ error }}
    </v-alert>

    <div v-if="previewUrl" class="preview-section">
      <h3 class="text-h6 mb-2">Preview</h3>
      <v-img
          :src="previewUrl"
          max-height="600"
          class="align-self-start border rounded"
          contain></v-img>
    </div>
  </v-container>
</template>

<style scoped>
.preview-section {
  width: 100%;
}
</style>