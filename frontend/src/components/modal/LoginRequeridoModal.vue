<template>
  <div
    v-if="aberto"
    class="modal-overlay"
    @click.self="$emit('fechar')"
  >
    <div class="modal-content fade-in-up text-center">
      <i
        class="ph ph-lock"
        style="font-size: 3rem; color: var(--text-secondary)"
      ></i>
      <h4 class="fw-bold mt-3">Faça login para comprar</h4>
      <p class="text-muted mb-4">
        Você precisa estar logado para adicionar produtos ao carrinho.
      </p>
      <RouterLink
        to="/login"
        class="btn-premium w-100 text-decoration-none d-block mb-3"
      >
        Fazer Login
      </RouterLink>
      <button
        class="btn btn-link text-muted text-decoration-none"
        @click="$emit('fechar')"
      >
        Continuar navegando
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  aberto: {
    type: Boolean,
    default: false,
  },
});

defineEmits(["fechar"]);
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.modal-content {
  background: var(--bg-color, #fff);
  width: 100%;
  max-width: 450px;
  border-radius: var(--radius-lg, 16px);
  padding: 2.5rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.fade-in-up {
  animation: fadeInUp 0.8s cubic-bezier(0.25, 1, 0.5, 1) forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
