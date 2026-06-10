<template>
  <div class="card shadow-sm border-0 h-100 cat-card text-center">
    <div class="img-container">
      <img
        v-if="categoria.tipoImagem"
        :src="imageUrl"
        :alt="categoria.nome"
        class="cat-image"
        @error="handleImageError"
      />
      <div
        v-else
        class="cat-image d-flex align-items-center justify-content-center bg-light"
      >
        <span class="text-muted small">Sem foto</span>
      </div>
    </div>

    <div class="card-body d-flex align-items-center justify-content-center p-3">
      <h5 class="card-title fw-bold m-0 fs-5">{{ categoria.nome }}</h5>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({ categoria: Object });

const imageUrl = computed(() => {
  return `http://localhost:8080/api/categorias/${props.categoria.id}/imagem?t=${new Date().getTime()}`;
});

const handleImageError = (event) => {
  event.target.outerHTML =
    '<div class="cat-image d-flex align-items-center justify-content-center bg-light"><span class="text-muted small">Sem foto</span></div>';
};
</script>

<style scoped>
.cat-card {
  cursor: pointer;
  transition: all 0.35s ease;
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
}

.cat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.08) !important;
}

.img-container {
  width: 100%;
  height: 240px;
  overflow: hidden;
  background: #f8f9fa;
}

.card-title {
  font-size: 1.3rem;
}

.cat-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.cat-card:hover .cat-image {
  transform: scale(1.04);
}
</style>
