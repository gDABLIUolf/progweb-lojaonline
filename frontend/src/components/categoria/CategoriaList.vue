<template>
  <section class="categories py-5">
    <div class="container text-center">
      <h2 class="mb-5">Explore por Categoria</h2>

      <p v-if="categorias.length === 0" class="text-muted mb-5">
        Nenhuma categoria cadastrada ainda.
      </p>

      <div v-else class="row g-4">
        <div
          class="col-md-4"
          v-for="categoria in categorias"
          :key="categoria.id"
        >
          <CategoriaCard :categoria="categoria" />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import CategoriaCard from "./CategoriaCard.vue";

defineProps({
  categorias: {
    type: Array,
    default: () => [],
  },
});
</script>

<style scoped>
.categories {
  background-color: transparent;
}

h2 {
  font-size: 2.2rem;
  font-weight: 600;
  letter-spacing: -0.02em;
}

@media (max-width: 768px) {
  h2 {
    font-size: 1.8rem;
  }
}
</style>
