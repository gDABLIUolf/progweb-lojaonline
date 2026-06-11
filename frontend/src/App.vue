<template>
  <RouterView />
  <ToastNotification />
</template>

<script setup>
import { onMounted } from "vue";
import ToastNotification from "./components/layout/ToastNotification.vue";

onMounted(() => {
  const savedTheme = localStorage.getItem("theme_vestebem");
  if (savedTheme === "dark") {
    document.documentElement.setAttribute("data-theme", "dark");
  } else {
    document.documentElement.removeAttribute("data-theme");
  }
});
</script>
