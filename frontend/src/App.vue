<script setup lang="ts">
</script>

<template>
  <v-app>
    <v-app-bar flat>
      <div class="d-flex align-center px-4">
        <v-app-bar-title shrink>C2PA</v-app-bar-title>
        <v-btn variant="plain" to="/verify" class="ml-4">Verify</v-btn>
        <v-btn variant="plain" to="/sign">Sign</v-btn>
      </div>
    </v-app-bar>

    <v-main>
      <router-view v-slot="{ Component }">
        <!--        <v-fade-transition hide-on-leave>-->
        <component :is="Component" />
        <!--        </v-fade-transition>-->
      </router-view>
    </v-main>
  </v-app>
</template>

<style scoped>
</style>
