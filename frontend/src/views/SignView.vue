<script setup lang="ts">
import { ref } from 'vue'

const title = ref('')
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)

const onFileChange = (file: File | File[] | null) => {
  const actualFile = Array.isArray(file) ? file[0] : file
  
  if (!actualFile) {
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
    }
    selectedFile.value = null
    previewUrl.value = null
    return
  }

  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
  }

  selectedFile.value = actualFile
  previewUrl.value = URL.createObjectURL(actualFile)
}

const onSign = async () => {
  if (!selectedFile.value || !title.value) return

  isLoading.value = true
  error.value = null

  const formData = new FormData()
  formData.append('title', title.value)
  formData.append('image', selectedFile.value)

  try {
    const response = await fetch('/api/c2pa/sign', {
      method: 'POST',
      body: formData,
    })

    if (!response.ok) {
      throw new Error(`Signing failed: ${response.statusText}`)
    }

    const blob = await response.blob()
    const url = URL.createObjectURL(blob)

    // Download the signed image
    const link = document.createElement('a')
    link.href = url
    // Use original filename or a default one
    const originalName = selectedFile.value.name
    const signedName = originalName.replace(/(\.[\w\d]+)$/, '_signed$1') || 'signed_image.jpg'
    link.download = signedName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    // Update preview with the signed image
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