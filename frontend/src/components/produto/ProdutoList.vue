<template>
  <section class="produtos py-5 bg-light">
    <div class="container px-4">
      <h2 class="text-center mb-5 titulo-secao">Todos os Produtos</h2>

      <div v-if="produtos.length === 0" class="text-center text-muted py-5">
        <i class="ph ph-package fs-1 mb-3"></i>
        <p>Nenhum produto encontrado.</p>
      </div>

      <div v-else class="row g-4">
        <div
          class="col-12 col-md-6 col-lg-4"
          v-for="produto in produtos"
          :key="produto.id"
        >
          <ProdutoCard
            :produto="produto"
            @adicionar-carrinho="$emit('adicionar-carrinho', $event)"
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import ProdutoCard from "./ProdutoCard.vue";

defineProps({
  produtos: {
    type: Array,
    default: () => [],
  },
});

defineEmits(["adicionar-carrinho"]);
</script>

<style scoped>
.titulo-secao {
  font-size: 2.2rem;
  font-weight: 600;
  color: #111;
}
</style>
