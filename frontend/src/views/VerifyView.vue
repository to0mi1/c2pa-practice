<script setup lang="ts">
import { ref } from 'vue'

interface ActionInfo {
  action: string
  digitalSourceType: string
}

interface ManifestHistory {
  label: string
  title: string
  signer: string
  issuer: string
  signedAt: string
  algorithm: string
  actions: ActionInfo[]
  claimGenerator: string
}

interface VerifyResponse {
  title: string
  signer: string
  issuer: string
  signedAt: string
  algorithm: string
  actions: ActionInfo[]
  validationState: string
  history: ManifestHistory[]
}

const selectedFile = ref<File | null>(null)
const previewUrl = ref<string | null>(null)
const verificationResult = ref<VerifyResponse | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)

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
      throw new Error(`Verification failed: ${response.statusText}`)
    }

    verificationResult.value = await response.json()
  } catch (err) {
    console.error('Error verifying image:', err)
    error.value = err instanceof Error ? err.message : 'An unknown error occurred'
  } finally {
    isLoading.value = false
  }
}

const onFileChange = (file: File | File[] | null) => {
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

        <div v-if="verificationResult.history && verificationResult.history.length > 1">
          <h3 class="text-h6 mt-6 mb-2">History</h3>
          <v-timeline side="end" density="compact">
            <v-timeline-item
                v-for="(h, index) in verificationResult.history"
                :key="index"
                size="small"
                :dot-color="index === 0 ? 'primary' : 'grey'"
            >
              <div class="text-subtitle-2">{{ h.title }}</div>
              <div class="text-caption">By {{ h.signer }}</div>
              <div class="text-caption">{{ new Date(h.signedAt).toLocaleString() }}</div>
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