<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = ref('verify')

watch(() => route.path, (path) => {
  if (path.includes('sign')) {
    activeTab.value = 'sign'
  } else {
    activeTab.value = 'verify'
  }
}, { immediate: true })
</script>

<template>
  <v-app>
    <v-app-bar flat border color="surface">
      <v-container class="d-flex align-center py-0">
        <v-icon color="primary" size="large" class="me-2">mdi-shield-check-outline</v-icon>
        <v-app-bar-title class="font-weight-bold">
          C2PA <span class="text-primary">Provenance</span>
        </v-app-bar-title>

        <v-spacer></v-spacer>

        <v-btn-toggle
            v-model="activeTab"
            variant="flat"
            density="compact"
            mandatory
            selected-class="bg-primary-lighten-4 text-primary"
        >
          <v-btn to="/verify" value="verify" prepend-icon="mdi-check-decagram-outline">Verify</v-btn>
          <v-btn to="/sign" value="sign" prepend-icon="mdi-fountain-pen-tip">Sign</v-btn>
        </v-btn-toggle>
      </v-container>
    </v-app-bar>

    <v-main class="bg-grey-lighten-4">
      <v-container>
        <router-view v-slot="{ Component }">
          <v-fade-transition hide-on-leave>
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<style scoped>
</style>
