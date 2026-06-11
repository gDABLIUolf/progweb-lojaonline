<template>
  <button
    class="btn-carrinho-flutuante"
    @click="$emit('abrir')"
    title="Ver carrinho"
  >
    <i class="ph ph-handbag"></i>
    <span v-if="quantidade > 0" class="flutuante-count">
      {{ quantidade }}
    </span>
  </button>
</template>

<script setup>
defineProps({
  quantidade: {
    type: Number,
    default: 0,
  },
});

defineEmits(["abrir"]);
</script>

<style scoped>
.btn-carrinho-flutuante {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  background: var(--primary-color, #111);
  color: white;
  border: none;
  border-radius: 12px 0 0 12px;
  width: 48px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.4rem;
  cursor: pointer;
  z-index: 9997;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
  transition: width 0.2s ease;
}

.btn-carrinho-flutuante:hover {
  width: 56px;
}

.flutuante-count {
  position: absolute;
  top: 6px;
  right: 6px;
  background: #e53e3e;
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}
</style>
