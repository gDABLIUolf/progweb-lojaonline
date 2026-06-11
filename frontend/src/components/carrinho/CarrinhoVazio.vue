<template>
  <div class="text-center py-5 fade-in-up">
    <!-- Carregando -->
    <template v-if="carregando">
      <div class="spinner-border text-dark" role="status"></div>
      <p class="mt-3 text-muted">A carregar o seu carrinho...</p>
    </template>

    <!-- Não logado -->
    <template v-else-if="!estaLogado">
      <i class="ph ph-lock" style="font-size: 4rem; color: var(--text-secondary)"></i>
      <h3 class="mt-3 fw-bold">Faça login para ver o seu carrinho</h3>
      <p class="text-muted mb-4">Você precisa estar logado para adicionar e ver produtos no carrinho.</p>
      <RouterLink to="/login" class="btn-premium px-5 py-3">Fazer Login</RouterLink>
    </template>

    <!-- Carrinho vazio -->
    <template v-else>
      <i class="ph ph-handbag" style="font-size: 4rem; color: var(--text-secondary)"></i>
      <h3 class="mt-3 fw-bold">O seu carrinho está vazio</h3>
      <p class="text-muted mb-4">Explore os nossos produtos e adicione algo que goste.</p>
      <RouterLink to="/" class="btn-premium px-5 py-3">Continuar a Comprar</RouterLink>
    </template>
  </div>
</template>

<script setup>
defineProps({
  carregando: {
    type: Boolean,
    default: false,
  },
  estaLogado: {
    type: Boolean,
    default: false,
  },
});
</script>

<style scoped>
.fade-in-up {
  animation: fadeInUp 0.6s ease forwards;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
